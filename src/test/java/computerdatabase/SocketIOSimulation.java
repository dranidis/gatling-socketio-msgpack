package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static computerdatabase.SocketIOHelper.*;

public class SocketIOSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .wsBaseUrl("ws://localhost:3000")
            .wsReconnect()
            .wsMaxReconnects(5)
            .wsAutoReplySocketIo4()
    //
    ;

    ScenarioBuilder scene = scenario("WebSocket")
            .exec(connectΤοSocketIo)
            .exec(debugSessionValues("sid", "server_sid", "namespace"))
            .repeat(10, "counter").on(
                    exec(sendMessageWithCheck(
                            "message",
                            "Hi #{counter}",
                            "broadcast",
                            "aResponse"))
                                    .exec(debugSessionValues("aResponse"))
                                    .pause(1))
            .exec(disconnectFromSocketIo);

    ScenarioBuilder adminScene = scenario("WebSocket admin")
            .exec(connectΤοSocketIo("admin"))
            .exec(debugSessionValues("sid", "server_sid", "namespace"))
            .doIf(session -> session.contains("server_sid"))
            .then(exec(sendMessageWithCheck(
                    "message",
                    "Hi",
                    "broadcast",
                    "adminResponse",
                    "admin"))
                            .exec(debugSessionValues("adminResponse"))
                            .exec(disconnectFromSocketIo("admin")));

    {
        setDebug(true);

        setUp(
                adminScene.injectOpen(atOnceUsers(1)
                // nothingFor(4), // 1
                // atOnceUsers(10), // 2
                // rampUsers(10).during(5), // 3
                // constantUsersPerSec(20).during(15), // 4
                // constantUsersPerSec(20).during(15).randomized(), // 5
                // rampUsersPerSec(10).to(100).during(10), // 6
                // rampUsersPerSec(10).to(200).during(10).randomized(), // 7
                // stressPeakUsers(1000).during(20) // 8  

                )
                //
                ,
                scene.injectOpen(atOnceUsers(1)
                // nothingFor(4), // 1
                // atOnceUsers(10), // 2
                // rampUsers(10).during(5), // 3
                // constantUsersPerSec(20).during(15), // 4
                // constantUsersPerSec(20).during(15).randomized(), // 5
                // rampUsersPerSec(10).to(100).during(10), // 6
                // rampUsersPerSec(10).to(200).during(10).randomized(), // 7
                // stressPeakUsers(1000).during(20) // 8  
                )

        ).protocols(httpProtocol);
    }
}
