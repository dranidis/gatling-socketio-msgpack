package socketio;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static socketio.SocketIOHelper.*;

import java.util.Map;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class SocketIOSimulationFeeder extends Simulation {

  HttpProtocolBuilder httpProtocol = http
      .wsBaseUrl("ws://localhost:3333")
      .wsReconnect()
      .wsMaxReconnects(5)
      .wsAutoReplySocketIo4();

  ScenarioBuilder sceneNoChecks = scenario("WebSocket no checks")
      // connect to the default namespace
      .exec(connectΤοSocketIo)
      .exec(debugSessionValues("sid", "server_sid", "namespace", "whole_message"))
      // repeat some times

      .feed(jsonFile("data.json").circular())

      .foreach("#{messages}", "message").on(
          exec(session -> {
            Map m = (Map) session.get("message");
            System.out.println(m.get("data"));
            return session;
          }).exec(debugSessionValues("message"))
              // exec(sendMessage(
              // "message",
              // "I am #{sid} counter: #{counter} time: #{time} data: #{data}"))
              .pause(1)
      //
      )

      // disconnect from the default namespace
      .exec(disconnectFromSocketIo);

  {
    setDebug(true);

    setUp(

        sceneNoChecks.injectOpen(atOnceUsers(3))

    ).protocols(httpProtocol);
  }
}
