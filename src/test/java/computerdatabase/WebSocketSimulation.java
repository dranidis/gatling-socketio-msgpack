package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class WebSocketSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:3000")
                        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .doNotTrackHeader("1")
                        .acceptLanguageHeader("en-US,en;q=0.5")
                        .acceptEncodingHeader("gzip, deflate")
                        .userAgentHeader("Gatling2")
                        // .wsBaseUrl("wss://ws.postman-echo.com");
                        .wsBaseUrl("ws://localhost:3000");

        ScenarioBuilder scene = scenario("WebSocket")
                        // .exec(http("firstRequest").get("/"))
                        .exec(ws("openSocket").connect("/")
                                        .onConnected(exec(ws("sendMessage").sendText("chat message Hi").await(20)
                                                        .on(ws.checkTextMessage("check1")
                                                                        .check(regex(".*Hi.*").saveAs("myMessage"))))))
                        .exec(session -> {
                                System.out.println(session);
                                return session;
                        }).exec(ws("closeConnection").close());

        {
                setUp(scene.injectOpen(atOnceUsers(1)).protocols(httpProtocol));

        }
}
