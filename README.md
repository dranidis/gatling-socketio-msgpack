# A demo maven project for load testing Socket.IO with Gatling

The simulation connects to a local Socket.IO server at port 3333.

## Socket.IO server

```sh
cd js/socketio-server
node index_simple.js
```

Output:

```
socket.io server listening on port 3333
```

Alternatively, you may run the server found in `js/express-socketio-server` that creates both a web server at `localhost:3333` and a socket-io server at the same port. The `index.html` page acts as a client for the socket-io server.

## Run

### Run a simulations

```sh
./mvnw gatling:test
```

or provide the simulation class (if there are more than one simulation classes)

```
./mvnw gatling:test  -Dgatling.simulationClass=socketio.SocketIOSimulation
```

### Run the Gatling Recorder

```sh
./mvnw gatling:recorder
```

> **IMPORTANT:** 
>
>The recorder does not capture websocket events. You have to find another way to record WS/Socket.IO communication.

## Configuration file

In file: `src/test/resources/gatling.conf`
you can uncomment the lines for which you wish to have DEBUG output.

## Gatling Maven project

Refer to the plugin documentation
[on the Gatling website](https://gatling.io/docs/current/extensions/maven_plugin/) for usage.

This project includes:

- [Maven Wrapper](https://maven.apache.org/wrapper/), so that you can immediately run Maven with `./mvnw` without having
  to install it on your computer
- minimal `pom.xml`
- latest version of `io.gatling:gatling-maven-plugin` applied
