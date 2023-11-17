package socketio;

import java.util.Arrays;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.Ws;
import io.gatling.javaapi.http.WsConnectActionBuilder;
import io.gatling.javaapi.http.WsSendTextActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.http.HttpDsl.ws;
import static socketio.ELBuilder.*;
import static io.gatling.javaapi.core.CoreDsl.exec;

/**
 * DSL for manipulating Socket.IO messages
 */
public class SocketIO {

  private Packet<String> packet;

  private Ws websocket;
  private String nameSpace;

  private SocketIO(Ws ws) {
    this(ws, "");
  }

  private SocketIO(Ws ws, String nameSpace) {
    this.websocket = ws;
    this.nameSpace = nameSpace;
    this.packet = TextFrame.getInstance();
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
    return (websocket.connect("/socket.io/?EIO=4&transport=websocket")
        .onConnected(exec(this.connectToNameSpace())));
  }

  private WsSendTextActionBuilder connectToNameSpace() {
    return websocket.sendText(packet.connectFrame(this.nameSpace));
  }

  public WsSendTextActionBuilder disconnect() {
    return websocket.sendText(packet.disconnectFrame(this.nameSpace));
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
  public WsSendTextActionBuilder sendTextSocketIO(String... arg) {
    return websocket.sendText(session -> {

      String[] frameArgs = Arrays.stream(arg)
          .map(s -> evaluateEL(session, s))
          .toArray(String[]::new);

      return packet.eventFrame(
          (StringBody(this.nameSpace)).apply(session),
          frameArgs);
    });
  }

  /**
   * Send a socket.io message. Receives a EL expression that evaluates to an
   * ArraySeq. Any element of the array that is a Map is converted to JSON.
   * 
   * @param elArray
   * @return
   */
  public WsSendTextActionBuilder sendTextSocketIO(String elArray) {
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
