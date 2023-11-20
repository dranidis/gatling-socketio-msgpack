package socketio.sim;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static socketio.SocketIO.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import socketio.SocketIO;
import socketio.protocols.DefaultSocketIOProtocolFactory;

public class SocketIOSimulationFeeder extends Simulation {

  HttpProtocolBuilder httpProtocol = http
      .wsBaseUrl("ws://localhost:3333")
      .wsReconnect()
      .wsMaxReconnects(5)
      .wsAutoReplySocketIo4();

  {
    SocketIO.setSocketIOProtocolFactory(new DefaultSocketIOProtocolFactory());
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

        sceneNoChecks.injectOpen(atOnceUsers(10))

    ).protocols(httpProtocol);
  }
}
