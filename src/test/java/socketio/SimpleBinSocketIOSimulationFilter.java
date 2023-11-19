package socketio;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.msgpack.jackson.dataformat.MessagePackMapper;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

/**
 * Connects to a socket-io server that uses the msgpack parser.
 * <p>
 * For an example server, run with the msgpack-express-socketio-server node
 * client in index.js
 */
public class SimpleBinSocketIOSimulationFilter extends Simulation {

  HttpProtocolBuilder httpProtocol = http
      .wsBaseUrl("ws://localhost:5555")
      .wsReconnect()
      .wsMaxReconnects(5)
      .wsAutoReplySocketIo4();

  byte[] packConnectBytes;
  byte[] packSendBytes;
  byte[] packDisconnectBytes;

  // Instantiate ObjectMapper for MessagePack
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

  /**
   * POJO for the packet object that is sent serialized to the server. Same packet
   * is deserialized from the server.
   */

  /**
   * A different POJO is needed for the disconnect packet because the server does
   * not expect the data field and responds with an error if it is present.
   */
  static class DisconnectPacket {
    public int type;
    public String nsp;

    public DisconnectPacket(String nsp) {
      type = 1;
      this.nsp = nsp;
    }
  }

  {
    // Filter out the data field from the disconnect packet
    SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
        .serializeAllExcept("data");
    FilterProvider filterData = new SimpleFilterProvider()
        .addFilter("filterData", theFilter);

    try {
      packConnectBytes = objectMapper.writeValueAsBytes(
          new SocketIOPacket(0, "/", Arrays.asList()));
      packSendBytes = objectMapper.writeValueAsBytes(
          new SocketIOPacket(2, "/", Arrays.asList("message", "hi")));
      packDisconnectBytes = objectMapper.writer(filterData).writeValueAsBytes(
          new SocketIOPacket(1, "/", Arrays.asList()));

      ObjectMapper mapper = new MessagePackMapper();
      Map<String, Object> deserialized = objectMapper
          .readValue(packDisconnectBytes, new TypeReference<Map<String, Object>>() {
          });
      System.out.println("packet: " + deserialized);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  CheckBuilder checkBroadcastEventSaveMessage = bodyBytes()
      .transform(bytes -> {
        SocketIOPacket packet = null;

        try {
          packet = objectMapper.readValue(bytes, SocketIOPacket.class);
        } catch (IOException e) {
          throw new RuntimeException(
              "Failed to deserialize incoming bytes to Packet object. ", e);
        }

        String eventName = packet.data.get(0);

        if (!eventName.equals("broadcast")) {
          throw new RuntimeException(
              "Expected broadcast event, got " + eventName + " instead");
        }

        String message = packet.data.get(1);
        return message;
      }).saveAs("response");

  ScenarioBuilder scene = scenario("WebSocket")
      .exec(ws("Connect WS")
          .connect("/socket.io/?EIO=4&transport=websocket"))
      .exec(ws("Connect to Socket.IO").sendBytes(packConnectBytes))
      .pause(1)
      .exec(ws("Say hi")
          .sendBytes(packSendBytes)
          .await(30).on(
              ws.checkBinaryMessage("checkMessage")
                  .check(checkBroadcastEventSaveMessage)))

      .exec(session -> {
        System.out.println("response: " + session.get("response"));
        return session;
      })
      .exec(ws("Disconnect from Socket.IO").sendBytes(packDisconnectBytes))
      .exec(ws("Close WS").close());

  {

    setUp(scene.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
