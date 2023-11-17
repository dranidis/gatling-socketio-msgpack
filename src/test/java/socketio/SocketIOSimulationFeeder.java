package socketio;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static socketio.SocketIOHelper.*;
import static socketio.SocketIO.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class SocketIOSimulationFeeder extends Simulation {

  HttpProtocolBuilder httpProtocol = http
      .wsBaseUrl("ws://localhost:3333")
      .wsReconnect()
      .wsMaxReconnects(5)
      .wsAutoReplySocketIo4();

  ScenarioBuilder sceneNoChecks = scenario("WebSocket no checks")
      // connect to the socket and the namespace
      //
      // should I read this from the data?
      .exec(connectΤοSocketIo("/events/live/en"))
      .exec(debugSessionValues("sid", "server_sid", "namespace", "whole_message"))

      // read messages from the data file
      // each message has a pause time, a namespace and a data field.
      // the data is an array of strings (e.g. event and message) and JSON objects
      .feed(jsonFile("data.json").circular())
      .foreach("#{messages}", "message").on(
          pause("#{message.pause}")
              .exec(socketIO("send Socket.IO message", "#{message.nsp}")
                  .sendTextSocketIO("#{message.data}")))
      // disconnect from the default namespace
      .exec(disconnectFromSocketIo);

  {
    setDebug(true);

    setUp(

        sceneNoChecks.injectOpen(atOnceUsers(2))

    ).protocols(httpProtocol);
  }
}
