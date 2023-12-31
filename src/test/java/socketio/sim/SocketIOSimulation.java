package socketio.sim;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static socketio.SocketIOHelper.*;
import static socketio.SocketIO.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class SocketIOSimulation extends Simulation {

  HttpProtocolBuilder httpProtocol = http
      .wsBaseUrl("ws://localhost:3333")
      .wsReconnect()
      .wsMaxReconnects(5)
      .wsAutoReplySocketIo4();

  {
    setDebug(true);
  }

  ScenarioBuilder scene = scenario("WebSocket")
      // connect to the default namespace
      .exec(socketIO("connect to socket.io").connect()
          .await(30)
          .on(checkWSConnectionMessageSID))
      .exec(socketIO("connect to namespace /")
          .connectToNameSpace("/")
          .await(30)
          .on(checkSocketIOConnectionMessageSID))

      .exec(debugSessionValues("sid", "server_sid", "namespace", "whole_message"))

      // repeat some times
      .repeat(5, "counter").on(
          // send a message to the default namespace and
          // expect a server message at the event broadcast
          // save the message at the session key aResponse
          exec(socketIO("send Socket.IO message")
              .send("message", "Hi #{counter}")
              .await(60)
              .on(checkEventMessage("broadcast", "aResponse")))
                  .exec(debugSessionValues("aResponse", "whole_message"))
                  .pause(1))
      // disconnect from the default namespace
      .exec(socketIO("disconnect from namespace /admin")
          .disconnectFromNameSpace("/"))
      .exec(socketIO("close").close());

  ScenarioBuilder sceneNoChecks = scenario("WebSocket no checks")
      // connect to the default namespace
      .exec(socketIO("connect to socket.io").connect())
      .exec(socketIO("connect to namespace /")
          .connectToNameSpace("/"))

      .feed(jsonFile("feed_data.json").circular())
      // .exec(debugSessionValues("sid", "server_sid", "namespace", "whole_message"))
      // repeat some times
      .repeat(2, "counter").on(
          // send a message to the default namespace 
          exec(socketIO("send Socket.IO message")
              .send(
                  "message", "I am counter: #{counter} time: #{time} data: #{data}"))
                      .pause(1))
      // disconnect from the default namespace
      .exec(socketIO("disconnect from namespace /admin")
          .disconnectFromNameSpace("/"))
      .exec(socketIO("close").close());

  ScenarioBuilder adminScene = scenario("WebSocket admin")
      // connect to the admin namespace
      .exec(socketIO("connect to socket.io").connect()
          .await(30)
          .on(checkWSConnectionMessageSID))
      .exec(socketIO("connect to namespace /admin")
          .connectToNameSpace("/admin")
          .await(30)
          .on(checkSocketIOConnectionMessageSID))

      .exec(debugSessionValues("sid", "server_sid", "namespace", "whole_message"))
      // do if the session contains the key server_sid
      .doIf(session -> session.contains("server_sid"))
      .then(
          // send a message to the admin namespace and
          // expect a server message at the event broadcast
          // save the message at the session key adminResponse
          exec(socketIO("send Socket.IO message", "admin")
              .send(
                  "message", "I am an admin")
              .await(30)
              .on(checkEventMessage("broadcast", "adminResponse")))
                  .exec(debugSessionValues("adminResponse", "whole_message"))
                  // disconnect from the admin namespace
                  .exec(
                      socketIO("disconnect from namespace /admin")
                          .disconnectFromNameSpace("/admin")))
      .exec(socketIO("close").close());

  {

    setUp(
        adminScene.injectOpen(

            atOnceUsers(1)

        // nothingFor(4), // 1
        // atOnceUsers(10), // 2
        // rampUsers(10).during(5), // 3
        // constantUsersPerSec(20).during(15), // 4
        // constantUsersPerSec(20).during(15).randomized(), // 5
        // rampUsersPerSec(10).to(100).during(10), // 6
        // rampUsersPerSec(10).to(200).during(10).randomized(), // 7
        // stressPeakUsers(1000).during(20) // 8

        ), scene.injectOpen(

            atOnceUsers(1)

        // nothingFor(4), // 1
        // atOnceUsers(10), // 2
        // rampUsers(10).during(5), // 3
        // constantUsersPerSec(20).during(15), // 4
        // constantUsersPerSec(20).during(15).randomized(), // 5
        // rampUsersPerSec(10).to(100).during(10), // 6
        // rampUsersPerSec(10).to(200).during(10).randomized(), // 7
        // stressPeakUsers(1000).during(20) // 8

        ), sceneNoChecks.injectOpen(
            atOnceUsers(1))

    ).protocols(httpProtocol);
  }
}
