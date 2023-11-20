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
   * Checks for a message: "40{"sid":"JnYOfDWvpdm1zrCEAAAB"}"
   * <p>
   * or: "40/admin,{"sid":"-DhxQ_lWflSzYiiJAAAP"}" where admin is a namespace
   * <p>
   * Saves the sid in the session: "server_sid"
   */
  public static WsFrameCheck checkSocketIOConnectionMessageSID = ws
      .checkTextMessage("check Socket.IO connection reply: save sid")
      .check(
          regex("^40.*\\{\"sid\":\"([^\"]+)").saveAs("server_sid"),
          regex("^40(.*),*\\{").saveAs("namespace"),
          regex("(.*)").saveAs("whole_message"));

  public static WsFrameCheck checkEventMessage(String eventName, String toSessionKey) {
    return ws.checkTextMessage("check server message after request")
        .check(
            regex("(.*)").saveAs("whole_message"),
            regex("42.*" + eventName + "...([^\"]*)").saveAs(toSessionKey));
  }

}
