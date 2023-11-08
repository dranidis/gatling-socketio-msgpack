package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class WebSocketSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        // .baseUrl("http://localhost:3000")
                        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        // .doNotTrackHeader("1")
                        // .acceptLanguageHeader("en-US,en;q=0.5")
                        // .acceptEncodingHeader("gzip, deflate")
                        // .userAgentHeader("Gatling2")
                        // .wsBaseUrl("wss://ws.postman-echo.com")
                        .wsBaseUrl("ws://localhost:3000")
                        .wsReconnect()
                        .wsMaxReconnects(5)
                        .wsAutoReplySocketIo4()
        //
        ;

        ScenarioBuilder scene = scenario("WebSocket")
                        .exec(http("firstRequest").get("/"))
                        .pause(1)
                        .exec(session -> session.set("id", "Gatling" + session.userId()))
                        .exec(ws("openSocket")
                                        //
                                        .connect("/socket.io/?EIO=4&transport=websocket&sid=#{id}")
                                        // .connect("/raw")
                                        //
                                        .onConnected(exec(session -> {
                                                System.out.println("CONNECTED");
                                                System.out.println(session.userId());
                                                return session;
                                        })))
                        .pause(1)
                        .exec(ws("sendMessage")
                                        .sendText("message Hi")
                                        .await(30).on(
                                                        ws.checkTextMessage("check1")
                                                                        .check(regex(".*Hi.*").saveAs(
                                                                                        "myMessage"))))
                        .exec(session -> {
                                System.out.println(session);
                                return session;
                        }).exec(ws("closeConnection").close());

        {
                setUp(scene.injectOpen(atOnceUsers(1)).protocols(httpProtocol));

        }
}
