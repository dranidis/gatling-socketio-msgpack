object SocketIoScenario {

    val httpProtocol =
    http
    .baseUrl(app_url)
    .wsBaseUrl(app_ws_url)
    .userAgentHeader(“gatling”)
    .wsAutoReplySocketIo4

    val scn =
    scenario(“SocketIo”)
    .exec(
        ws(“Connect to WS”)
        .connect(“/socket.io/?EIO=4&transport=websocket&roomName=LIG:IDFM:C01742”)
        .onConnected(
            exec { session =>
            println(“Connected”)
            session
            }
        )
        .await(60)
            (wsCheck)
    )
    .pause(120)
    .exec{ session =>
        println(“Disconnect”)
        ws(“Close WS”).close
        session
    }

    private def wsCheck = {
        ws.checkTextMessage(“check-received-message”).check(regex(“.vehicleActivity.”).saveAs(“message”))
    }

}