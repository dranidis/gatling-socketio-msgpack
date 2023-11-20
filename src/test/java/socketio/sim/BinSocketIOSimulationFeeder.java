package socketio.sim;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static socketio.SocketIO.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import socketio.SocketIO;
import socketio.protocols.MsgpackSocketIOProtocolFactory;

public class BinSocketIOSimulationFeeder extends Simulation {

  HttpProtocolBuilder httpProtocol = http
      .wsBaseUrl("ws://localhost:5555")
      .wsReconnect()
      .wsMaxReconnects(5)
      .wsAutoReplySocketIo4();

  {
    SocketIO.setSocketIOProtocolFactory(new MsgpackSocketIOProtocolFactory());
  }

  String namespace = "/events/live/en";

  ScenarioBuilder sceneNoChecks = scenario("WebSocket no checks")
      // connect to the socket and the namespace
      //
      // should I read this from the data?
      // does it connect to other namespaces?
      //
      .exec(socketIO("connect to socket.io").connect())
      .exec(socketIO("connect to namespace")
          .connectToNameSpace(namespace))

      // read messages from the data file
      // each message has a pause time, a namespace and a data field.
      // the data is an array of strings (e.g. event and message) and JSON objects
      .feed(jsonFile("data.json").circular())
      .foreach("#{messages}", "message").on(
          pause("#{message.pause}")
              .exec(socketIO("send message", namespace)
                  .send("#{message.data}")))

      // disconnect from the default namespace
      .exec(socketIO("disconnect from namespace")
          .disconnectFromNameSpace(namespace))
      .exec(socketIO("close").close());

  {

    setUp(

        sceneNoChecks.injectOpen(

            atOnceUsers(1)

        // ,

        // nothingFor(4), // 1
        // atOnceUsers(10), // 2
        // rampUsers(10).during(5)
        // , // 3
        // constantUsersPerSec(20).during(15)
        // , // 4
        // constantUsersPerSec(20).during(15).randomized(), // 5
        // rampUsersPerSec(10).to(100).during(10), // 6
        // rampUsersPerSec(10).to(200).during(10).randomized(), // 7
        // stressPeakUsers(1000).during(20) // 8

        )).protocols(httpProtocol);
  }
}
