package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class WebSocketSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:3000")
            // .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            // .doNotTrackHeader("1")
            // .acceptLanguageHeader("en-US,en;q=0.5")
            // .acceptEncodingHeader("gzip, deflate")
            // .userAgentHeader("Gatling2")
            // .wsBaseUrl("wss://ws.postman-echo.com")
            .wsBaseUrl("ws://localhost:3000")
            // .wsReconnect()
            // .wsMaxReconnects(5)
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

    ChainBuilder handshake = //
            exec(http("handshake")
                    .get("/socket.io/?EIO=4&transport=polling&t=" + timestamp)
                    .check(status().is(200),
                            regex("\"sid\":\"([^\"]+)").saveAs("sid")) //
            ).exec(session -> printSessionValue(session, "sid"));

    ChainBuilder postPolling = //
            exec(http("postPolling")
                    .post("/socket.io/?EIO=4&transport=polling&t=" + timestamp + "&sid=#{sid}")
                    .body(StringBody("40"))
                    .check(status().is(200),
                            regex(".*").saveAs("response")) //
            ).exec(session -> printSessionValue(session,
                    "response") //
            );

    ChainBuilder getPolling = exec(http("getPolling")
            .get("/socket.io/?EIO=4&transport=polling&t="
                    + timestamp
                    + "&sid=#{sid}")
            .check(status().is(200),
                    regex(".*")
                            .saveAs("response2")) //
    ).exec(session -> printSessionValue(session,
            "response2") //
    )
    // .pause(5 //
    // ).exec(http("polling4")
    //         .get("/socket.io/?EIO=4&transport=websocket&t="
    //                 + timestamp
    //                 + "&sid=#{sid}")
    //         .check(status().is(200),
    //                 regex(".*")
    //                         .saveAs("response3")) //
    // ).exec(session -> printSessionValue(session,
    //         "response3")
    //         )
    ;

    ChainBuilder upgradeToWebSocket = exec(http("upgdrade")
            .get("/socket.io/?EIO=4&transport=websocket&t="
                    + timestamp
                    + "&sid=#{sid}")
            .check(status().is(101),
                    regex(".*")
                            .saveAs("response3")) //
    ).exec(session -> printSessionValue(session,
            "response3"));

    public static ChainBuilder connectToURL(String url, WsFrameCheck checkMessage) {
        System.out.println("connectToURL: " + url);
        System.out.println("#{sid}");
        return exec(ws("connect")
                .connect(url).await(30).on(checkMessage)
                // .connect("/socket.io/?EIO=4&transport=websocket")

                // .await(5).on(wsCheck)
                .onConnected(exec(session -> {
                    System.out.println("CONNECTED");
                    // System.out.println("" + session.get("connectionResponse"));
                    return session;
                }) //
                   // .exec(ws("sendAfterConnection")
                   //         .sendText("[\"message\", \"b\"]"))
                ));
    }

    public static ChainBuilder sendMessage(String sendString) {
        return exec(ws("sendMessage")
                .sendText(sendString));
        // .await(30).on(
        //         ws.checkTextMessage("checkMessage")
        //                 .check(regex(".*").saveAs("myMessage"))))
        //                         .pause(10)

    }

    WsFrameCheck checkMessage = ws.checkTextMessage("checkMessage")
            .check(regex(".*").saveAs("myMessage"));

    public static ChainBuilder sendMessage(String sendString, int time, WsFrameCheck wsCheck) {
        return exec(ws("sendMessage")
                .sendText(sendString).await(time).on(wsCheck));
    }

    ScenarioBuilder scene = scenario("WebSocket")
            // .exec(handshake, postPolling, getPolling)
            // .pause(1)
            // .exec(getPolling)
            // .exec(upgradeToWebSocket)
            // .pause(2)
            // .exec(upgradeToWebSocket)

            // .exec(connectToURL("/socket.io/?EIO=4&transport=websocket&sid=#{sid}"))
            .exec(connectToURL("/socket.io/?EIO=4&transport=websocket", checkMessage))
            .exec(session -> printSessionValue(session, "myMessage"))

            // .pause(10)
            .exec(sendMessage("40", 15, checkMessage)) // connect to socket.io

            .exec(session -> printSessionValue(session, "myMessage"))

            .repeat(10, "counter").on(
                    sendMessage("42[\"message\",\"Hi #{counter}\"]", 10, checkMessage)
                            .exec(session -> printSessionValue(session, "myMessage")))
            .pause(10)

            .exec(sendMessage("41")) // disconnect from socket.io
            .exec(ws("closeConnection").close())
    //
    ;

    {
        setUp(scene.injectOpen(atOnceUsers(100)).protocols(httpProtocol));
    }
}
