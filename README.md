# A demo maven project for load testing Socket.IO with Gatling

## Socket.IO server

```sh
cd js/socketio-server
node index_simple.js

```

Output:

```
socket.io server listening on port 3000
```

## Run

### Run a simulations

```sh
./mvnw gatling:test
```

or provide the simulation class (if there are other simulation classes)

```
./mvnw gatling:test  -Dgatling.simulationClass=computerdatabase.WebsocketExampleSimulation
```

### Run the Gatling Recorder

```sh
./mvnw gatling:recorder
```

The recorder does not capture websocket events.

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
