package socketio;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackMapper;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

/**
 * Run with the msgpack-express-socketio-server node client in index.js
 */
public class SimpleBinSocketIOSimulation extends Simulation {

  HttpProtocolBuilder httpProtocol = http
      .wsBaseUrl("ws://localhost:5555")
      .wsReconnect()
      .wsMaxReconnects(5)
      .wsAutoReplySocketIo4();

  byte[] connectionBytes;
  byte[] messageBytes;
  byte[] disconnectBytes;

  {
    // Instantiate ObjectMapper for MessagePack
    ObjectMapper objectMapper = new MessagePackMapper();

    // Serialize a Map to byte array
    Map<String, Object> connectionMap = new HashMap<>();
    connectionMap.put("type", 0);
    connectionMap.put("nsp", "/");

    Map<String, Object> messageMap = new HashMap<>();
    messageMap.put("type", 2);
    messageMap.put("data", new String[] { "message", "Hi" });
    messageMap.put("nsp", "/");

    Map<String, Object> disconnectMap = new HashMap<>();
    disconnectMap.put("type", 1);
    disconnectMap.put("nsp", "/");

    try {
      connectionBytes = objectMapper.writeValueAsBytes(connectionMap);
      messageBytes = objectMapper.writeValueAsBytes(messageMap);
      disconnectBytes = objectMapper.writeValueAsBytes(disconnectMap);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  ScenarioBuilder scene = scenario("WebSocket")
      .exec(ws("Connect WS").connect("/socket.io/?EIO=4&transport=websocket"))
      .pause(1)
      .exec(ws("Connect to Socket.IO")
          .sendBytes(connectionBytes))

      .pause(1)
      .exec(ws("Say hi")
          .sendBytes(messageBytes)

          .await(30).on(
              ws.checkBinaryMessage("checkMessage")
                  .check(bodyBytes().transform(bytes -> {

                    // Deserialize the byte array to a Map
                    Map<String, Object> deserialized = null;
                    ObjectMapper objectMapper = new MessagePackMapper();

                    try {
                      deserialized = objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {
                      });
                    } catch (IOException e) {
                      e.printStackTrace();
                    }

                    @SuppressWarnings("unchecked")
                    List<String> dataList = (List<String>) deserialized.get("data");
                    String msg = dataList.get(1);

                    return msg;
                  }).saveAs("response"))))

      .exec(session -> {
        System.out.println("response: " + session.get("response"));
        return session;
      }).exec(ws("Disconnect from Socket.IO")

          .sendBytes(disconnectBytes))

      // .sendText("41"))

      .exec(ws("Close WS").close())
  //
  ;

  {
    setUp(scene.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
