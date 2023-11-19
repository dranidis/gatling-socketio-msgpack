package socketio.sim;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

/**
 * This is the example that is posted on:
 * https://dranidis.github.io/posts/gatling-socketio/
 */
public class SimpleSocketIOSimulation extends Simulation {

  HttpProtocolBuilder httpProtocol = http
      .wsBaseUrl("ws://localhost:3333")
      .wsReconnect()
      .wsMaxReconnects(5)
      .wsAutoReplySocketIo4();

  ScenarioBuilder scene = scenario("WebSocket")
      .exec(ws("Connect WS").connect("/socket.io/?EIO=4&transport=websocket"))
      .exec(ws("Connect to Socket.IO").sendText("40"))
      .pause(1)
      .exec(ws("Say hi")
          .sendText("42[\"message\",\"Hi\"]")
          .await(30).on(
              ws.checkTextMessage("checkMessage")
                  .check(regex(".*broadcast...([^\"]*)")
                      .saveAs("response"))))
      .exec(session -> {
        System.out.println("response: " + session.get("response"));
        return session;
      })
      .exec(ws("Disconnect from Socket.IO").sendText("41"))
      .exec(ws("Close WS").close());

  {
    setUp(
        scene.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
