package socketio;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.Ws;
import io.gatling.javaapi.http.WsAwaitActionBuilder;
import io.gatling.javaapi.http.WsConnectActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.http.HttpDsl.ws;
import static socketio.ELBuilder.*;

/**
 * DSL for manipulating Socket.IO messages
 */
public class SocketIO {

  private PacketFrame<String> packet;

  private Ws websocket;
  private String nameSpace;
  private SocketIOProtocol socketIOProtocol;

  private SocketIO(Ws webSocket) {
    this(webSocket, "");
  }

  private SocketIO(Ws webSocket, String nameSpace) {
    this.websocket = webSocket;
    this.nameSpace = nameSpace;
    this.packet = TextFrame.getInstance();

    this.socketIOProtocol = new DefaultSocketIOProtocol(this.websocket);
  }

  /**
   * Bootstrap the SocketIO builder with the default namespace
   * 
   * @param name
   * @return
   */
  public static SocketIO socketIO(String name) {
    return SocketIO.socketIO(name, "");
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

  public WsConnectActionBuilder connect() {
    return (websocket.connect("/socket.io/?EIO=4&transport=websocket"));
    // .onConnected(exec(...)));
  }

  public WsAwaitActionBuilder connectToNameSpace(String nameSpace) {
    return socketIOProtocol.send(
        new SocketIOPacket(0, nameSpace, Arrays.asList()));
  }

  public WsAwaitActionBuilder disconnectFromNameSpace(String nameSpace) {
    return socketIOProtocol.send(
        new SocketIOPacket(1, nameSpace, Arrays.asList()));
  }

  public ActionBuilder close() {
    return websocket.close();
  }

  /**
   * Send a socket.io message. Receives multiple EL expressions that evaluate to
   * strings.
   * 
   * @param arg
   * @return
   */
  public WsAwaitActionBuilder sendTextSocketIO(String... arg) {
    return socketIOProtocol.send(session -> {

      // create the SocketIOPacket

      List<String> frameArgs = Arrays.stream(arg)
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
  public WsAwaitActionBuilder sendTextSocketIO(String elArray) {
    return websocket.sendText(session -> {

      int size = getSize(session, elArray);
      String[] frameArgs = new String[size];

      for (int i = 0; i < size; i++) {
        String element = atIndex(session, elArray, i);

        if (element.startsWith("Map")) {
          element = mapToJSON(session, elArray, i);
        }

        frameArgs[i] = element;
      }

      return packet.eventFrame(
          (StringBody(this.nameSpace)).apply(session),
          frameArgs);
    });
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
