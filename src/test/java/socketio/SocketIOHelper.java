package socketio;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class SocketIOHelper {

  private static boolean isDebug = false;

  // Instantiate ObjectMapper for MessagePack
  private static ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

  public static void setDebug(boolean debug) {
    System.out.println("DEBUG is " + debug);
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

  public static CheckBuilder checkData = bodyBytes().transform(bytes -> {
    SocketIOPacket packet = null;

    try {
      packet = objectMapper.readValue(bytes, SocketIOPacket.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    /**
     * first element in the data array is the event name and the second element is
     * the message
     */
    List<String> data = packet.getData();

    data.stream().forEach(System.out::println);

    return data;
  }).saveAs("response");

  public static CheckBuilder checkConnectionNamespace = bodyBytes().transform(bytes -> {
    TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
    };
    Map<String, Object> xs = null;
    try {
      xs = objectMapper.readValue(bytes, typeReference);
      // xs.entrySet().stream().forEach(System.out::println);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return xs.get("data");
  }).saveAs("response");

}
