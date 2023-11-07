package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class WebSocketSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            // .baseUrl("http://demos.kaazing.com")
            .wsBaseUrl("wss://echo.websocket.org");

    ScenarioBuilder scene = scenario("WebSocket")
            // .exec(http("firstRequest").get("/"))
            .exec(ws("openSocket").connect("/echo")
                    .onConnected(
                            exec(
                                    ws("sendMessage")
                                            .sendText("knoldus")
                                            .await(20).on(
                                                    ws.checkTextMessage("check1")
                                                            .check(regex(".*knoldus.*").saveAs("myMessage"))))))
            .exec(
                    session -> {
                        System.out.println(session);
                        return session;
                    })
            .exec(
                    ws("closeConnection").close());

    {
        setUp(scene.injectOpen(atOnceUsers(1)).protocols(httpProtocol));

    }
}
