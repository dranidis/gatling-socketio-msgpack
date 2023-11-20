package socketio.rawsim;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

/**
 * Connects to a socket-io server that uses the msgpack parser.
 * <p>
 * For an example server, run with the msgpack-express-socketio-server node
 * client in index.js
 */
public class SimpleBinSocketIOSimulation extends Simulation {

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
  static class Packet {
    public int type;
    public String nsp;
    public List<String> data;

    public Packet() {
    }

    public Packet(int type, String nsp, List<String> data) {
      this.type = type;
      this.nsp = nsp;
      this.data = data;
    }
  }

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
    try {
      packConnectBytes = objectMapper.writeValueAsBytes(
          new Packet(0, "/", Arrays.asList()));
      packSendBytes = objectMapper.writeValueAsBytes(
          new Packet(2, "/", Arrays.asList("message", "hi")));
      packDisconnectBytes = objectMapper.writeValueAsBytes(
          new DisconnectPacket("/"));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  CheckBuilder checkBroadcastEventSaveMessage = bodyBytes().transform(bytes -> {
    Packet packet = null;

    try {
      packet = objectMapper.readValue(bytes, Packet.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    /**
     * first element in the data array is the event name and the second element is
     * the message
     */
    String eventName = packet.data.get(0);
    String message = packet.data.get(1);

    if (!eventName.equals("broadcast")) {
      throw new RuntimeException("Expected broadcast event, got " + eventName + " instead");
    }

    return message;
  }).saveAs("response");

  ScenarioBuilder scene = scenario("WebSocket")
      .exec(ws("Connect WS").connect("/socket.io/?EIO=4&transport=websocket"))
      .pause(1)
      .exec(ws("Connect to Socket.IO").sendBytes(packConnectBytes))

      .pause(1)
      .exec(ws("Say hi").sendBytes(packSendBytes)

          .await(30).on(
              ws.checkBinaryMessage("check broadcast event in message")
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
