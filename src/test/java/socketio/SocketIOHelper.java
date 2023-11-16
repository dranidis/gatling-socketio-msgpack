package socketio;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class SocketIOHelper {

  private static boolean isDebug = false;

  public static void setDebug(boolean debug) {
    isDebug = debug;
  }

  public static ChainBuilder debugSessionValues(String... key) {
    return exec(session -> {
      if (isDebug) {
        String[] keyValues = new String[key.length];

        int i = 0;
        for (String k : key) {
          keyValues[i++] = k + ": " + session.get(k);
        }

        System.out.println("SESSION " + String.join(", ", keyValues));
      }
      return session;
    });
  }

  /**
   * Checks for a message (without the line changes):
   * 
   * <pre>
   * "0{"sid":"_9SxGdR_2ReMDY5bAAAA", 
   *    "upgrades":[],
   *    "pingInterval":25000, 
   *    "pingTimeout":20000, 
   *    "maxPayload":1000000}"
   * </pre>
   * 
   * Saves the sid in the session: "sid"
   */
  public static WsFrameCheck checkWSConnectionMessageSID = ws
      .checkTextMessage("check Socket.IO connection reply: save sid")
      .check(regex("^0\\{\"sid\":\"([^\"]+)").saveAs("sid"),
          regex("(.*)").saveAs("whole_message"));

  /**
   * Checks for a mesage: "40{"sid":"JnYOfDWvpdm1zrCEAAAB"}"
   * <p>
   * or: "40/admin,{"sid":"-DhxQ_lWflSzYiiJAAAP"}" where admin is a namespace
   * <p>
   * Saves the sid in the session: "server_sid"
   */
  public static WsFrameCheck checkSocketIOConnectionMessageSID = ws
      .checkTextMessage("check Socket.IO connection reply: save sid")
      .check(regex("^40.*\\{\"sid\":\"([^\"]+)").saveAs("server_sid"),
          regex("^40(.*),*\\{").saveAs("namespace"),
          regex("(.*)").saveAs("whole_message"));

  static WsFrameCheck checkEventMessage(String eventName, String toSessionKey) {
    return ws.checkTextMessage("check server message after request")
        .check(regex(".*" + eventName + "...([^\"]*)")
            .saveAs(toSessionKey),
            regex("(.*)").saveAs("whole_message"));
  }

  /**
   * First connects to the web socket and expects a message with the session id.
   * On successful connection, sends a CONNECT message to the Socket.IO and
   * expects a message with the server session id.
   */
  public static ChainBuilder connectΤοSocketIo(String nameSpace) {
    return exec(ws("connect to Socket.IO")
        .connect("/socket.io/?EIO=4&transport=websocket")
        .await(30)
        .on(checkWSConnectionMessageSID)
        .onConnected(exec(ws("Connect to Socket.IO")
            .sendText(connectFrame(nameSpace))
            .await(30)
            .on(checkSocketIOConnectionMessageSID))))
        /*
         * a pause was required to avoid
         * subsequent regex mismatch
         * seems to work now
         */
        .pause(1);
  }

  /**
   * Connects to the default namespace: "/"
   */
  public static ChainBuilder connectΤοSocketIo = connectΤοSocketIo("");

  /**
   * Disconnects from the Socket.IO namespace
   * 
   * @param nameSpace
   * @return
   */
  public static ChainBuilder disconnectFromSocketIo(String nameSpace) {
    return exec(ws("Disconnect from Socket.IO")
        .sendText(disconnectFrame(nameSpace)))
        .exec(ws("Close WS").close());
  }

  /**
   * Disconnects from the default namespace: "/"
   */
  public static ChainBuilder disconnectFromSocketIo = disconnectFromSocketIo("");

  static ChainBuilder sendMessage(String eventName, String message, String nameSpace) {
    return exec(ws("send Socket.IO message")
        .sendText(eventFrame(eventName, message, nameSpace)));
  }

  static ChainBuilder sendMessage(String eventName, String message) {
    return sendMessage(eventName, message, "");
  }

  /**
   * Sends a message to the Socket.IO server and expects a message with the
   * responseEventName as an event name and saves the message in the session with
   * the toSessionKey.
   * 
   * @param eventName
   * @param message
   * @param responseEventName
   * @param toSessionKey
   * @param nameSpace
   * @return
   */
  static ChainBuilder sendMessageWithCheck(
      String eventName,
      String message,
      String responseEventName,
      String toSessionKey,
      String nameSpace) {
    return exec(ws("send Socket.IO message")
        /*
         * 4 => the Engine.IO message type
         * 2 => the Socket.IO EVENT type
         */
        .sendText(eventFrame(eventName, message, nameSpace))
        .await(30)
        .on(checkEventMessage(responseEventName, toSessionKey)));
  }

  /**
   * Sends a message to the default namespace of Socket.IO server and expects a
   * message with the responseEventName as an event name and saves the message in
   * the session with the toSessionKey.
   * 
   * @param eventName
   * @param message
   * @param responseEventName
   * @param toSessionKey
   * @return
   */
  static ChainBuilder sendMessageWithCheck(
      String eventName,
      String message,
      String responseEventName,
      String toSessionKey) {
    return sendMessageWithCheck(
        eventName,
        message,
        responseEventName,
        toSessionKey,
        "");
  }

  /*
   * PRIVATE METHODS
   */

  /*
   * 4 => the Engine.IO message type
   * 0 => the Socket.IO CONNECT type
   * 1 => the Socket.IO DISCONNECT type
   * 2 => the Socket.IO EVENT type
   */
  private static final String CONNECT_FRAME = "40";
  private static final String DISCONNECT_FRAME = "41";
  private static final String EVENT_FRAME = "42";

  /**
   * The default namespace is "/". "" can also be used to represent the default
   * 
   * @param nameSpace
   * @return
   */
  private static boolean notDefaultNamespace(String nameSpace) {
    return !nameSpace.equals("/") && !nameSpace.equals("");
  }

  private static String textFrame(String text, String nameSpace) {
    String frame = text;

    if (notDefaultNamespace(nameSpace)) {
      frame += "/" + nameSpace;

      if (text.equals(EVENT_FRAME)) {
        frame += ",";
      }
    }

    return frame;
  }

  private static String eventFrame(String eventName, String message, String nameSpace) {
    return textFrame(EVENT_FRAME, nameSpace)
        + "[\"" + eventName + "\",\"" + message + "\"]";
  }

  private static String connectFrame(String nameSpace) {
    return textFrame(CONNECT_FRAME, nameSpace);
  }

  private static String disconnectFrame(String nameSpace) {
    return textFrame(DISCONNECT_FRAME, nameSpace);
  }
}
