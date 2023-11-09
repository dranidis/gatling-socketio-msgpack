package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class WebSocketSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:3000")
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

        private WsFrameCheck wsCheck = ws.checkTextMessage("checkConnection")
                        .check(regex("(.*)").saveAs("connectionResponse"));;

        private static final String timestamp = String.valueOf(System.currentTimeMillis());

        private static Session printSessionValue(Session session, String key) {
                System.out.println("session: " + key + ": " + session.get(key));
                return session;
        }

        ChainBuilder handshake = exec(http("handshake")
                        .get("/socket.io/?EIO=4&transport=polling&t=" + timestamp)
                        .check(status().is(200),
                                        regex("\"sid\":\"([^\"]+)").saveAs("sid")))
                                                        .exec(session -> printSessionValue(session, "sid"));

        ChainBuilder pollingChain2 = exec(
                        http("polling2").post("/socket.io/?EIO=4&transport=polling&t=" + timestamp + "&sid=#{sid}")
                                        .body(StringBody("40"))
                                        .check(status().is(200),
                                                        regex(".*").saveAs("response")))
                                                                        .exec(session -> printSessionValue(session,
                                                                                        "response"))
                                                                        .exec(http("polling3")
                                                                                        .get("/socket.io/?EIO=4&transport=polling&t="
                                                                                                        + timestamp
                                                                                                        + "&sid=#{sid}")
                                                                                        .check(status().is(200),
                                                                                                        regex(".*")
                                                                                                                        .saveAs("response2")))
                                                                        .exec(session -> printSessionValue(session,
                                                                                        "response2"));

        ScenarioBuilder scene = scenario("WebSocket")
                        .exec(handshake, pollingChain2)
                        .exec(ws("openSocket")
                                        //
                                        .connect("/socket.io/?EIO=4&transport=websocket&sid=#{sid}")
                                        // .await(5).on(wsCheck)

                                        // .connect("/raw")
                                        //
                                        .onConnected(exec(session -> {
                                                System.out.println("CONNECTED");
                                                // System.out.println("" + session.get("connectionResponse"));
                                                return session;
                                        })))
                        .exec(ws("sendMessage")
                                        .sendText("[\"message\", \"a\"]")
                                        .await(30).on(
                                                        ws.checkTextMessage("checkMessage")
                                                                        .check(regex(".*").saveAs("myMessage"))))
                        .exec(session -> printSessionValue(session, "myMessage"))
                        .exec(ws("closeConnection").close());

        {

                setUp(scene.injectOpen(atOnceUsers(1)).protocols(httpProtocol));

        }
}
