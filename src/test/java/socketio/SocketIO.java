package socketio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.Ws;
import io.gatling.javaapi.http.WsAwaitActionBuilder;
import socketio.protocols.DefaultSocketIOProtocolFactory;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.http.HttpDsl.ws;
import static socketio.ELBuilder.*;

/**
 * DSL for manipulating Socket.IO messages
 */
public class SocketIO {

  private Ws webSocket;
  private String nameSpace;
  private SocketIOProtocol socketIOProtocol;
  private static SocketIOProtocolFactory socketIOProtocolFactory = new DefaultSocketIOProtocolFactory();

  public static void setSocketIOProtocolFactory(SocketIOProtocolFactory socketIOProtocolFactory) {
    SocketIO.socketIOProtocolFactory = socketIOProtocolFactory;
  }

  private SocketIO(Ws webSocket) {
    this(webSocket, "");
  }

  private SocketIO(Ws webSocket, String nameSpace) {
    this.webSocket = webSocket;
    this.nameSpace = nameSpace;
    this.socketIOProtocol = socketIOProtocolFactory.createSocketIOProtocol(this.webSocket);
  }

  /**
   * Bootstrap the SocketIO builder with the default namespace
   * 
   * @param name
   * @return
   */
  public static SocketIO socketIO(String name) {
    return SocketIO.socketIO(name, "/");
  }

  /**
   * Bootstrap the SocketIO builder with a namespace
   * 
   * @param name
   * @param nameSpace
   * @return
   */
  public static SocketIO socketIO(String name, String nameSpace) {
    return new SocketIO(ws(name), nameSpace);
  }

  public WsAwaitActionBuilder<?, ?> connect() {
    return (webSocket.connect(
        "/socket.io/?EIO=4&transport=websocket"));
    // .onConnected(exec(...)));
  }

  public WsAwaitActionBuilder<?, ?> connectToNameSpace(String nameSpace) {
    return socketIOProtocol.send(
        new SocketIOPacket(
            SocketIOType.CONNECT.getValue(),
            nameSpace,
            Arrays.asList()));
  }

  public WsAwaitActionBuilder<?, ?> disconnectFromNameSpace(String nameSpace) {
    return socketIOProtocol.send(
        new SocketIOPacket(
            SocketIOType.DISCONNECT.getValue(),
            nameSpace,
            Arrays.asList()));
  }

  public ActionBuilder close() {
    return webSocket.close();
  }

  /**
   * Send a socket.io message. Receives multiple EL expressions that evaluate to
   * strings.
   * 
   * @param arg
   * @return
   */
  public WsAwaitActionBuilder<?, ?> send(String... arg) {
    return socketIOProtocol.send(session -> {

      // create and return the SocketIOPacket

      List<Object> frameArgs = Arrays.stream(arg)
          .map(s -> evaluateEL(session, s))
          .collect(Collectors.toList());

      return new SocketIOPacket(2, this.nameSpace, frameArgs);
    });
  }

  /**
   * Send a socket.io message. Receives a EL expression that evaluates to an
   * ArraySeq. Any element of the array that is a Map is converted to JSON.
   * 
   * @param elArray
   * @return
   */
  public WsAwaitActionBuilder<?, ?> send(String elArray) {
    return socketIOProtocol.send(session -> {

      // create and return the SocketIOPacket

      int size = getSize(session, elArray);
      List<Object> frameArgsList = new ArrayList<>();

      for (int i = 0; i < size; i++) {
        String element = atIndex(session, elArray, i);

        Object elementToAdd = null;

        // Maps in EL string representation start with the
        // word "Map". If the element is a Map, convert it to
        // Map<String, Object>.
        if (element.startsWith("Map")) {
          String jsonString = mapToJSON(session, elArray, i);
          elementToAdd = parseJsonToMap(jsonString);
        } else {
          elementToAdd = element;
        }
        frameArgsList.add(elementToAdd);

      }
      return new SocketIOPacket(2, evaluateEL(session, this.nameSpace), frameArgsList);
    });
  }

  private Map<String, Object> parseJsonToMap(String json) {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map = new HashMap<>();

    try {
      map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return map;
  }

  private String mapToJSON(Session session, String arg, int i) {
    return evaluateEL(session,
        el(arg)
            .getAttribute()
            .append("(" + i + ").jsonStringify()")
            .toString());
  }

  private Integer getSize(Session session, String arg) {
    return Integer.valueOf(evaluateEL(session,
        el(arg)
            .getAttribute()
            .append(".size()")
            .toString()));
  }

  private String atIndex(Session session, String arg, int i) {
    return evaluateEL(session,
        el(arg)
            .getAttribute()
            .append("(" + i + ")")
            .toString());
  }

  private String evaluateEL(Session session, String el) {
    return (StringBody(el)).apply(session);
  }

}
