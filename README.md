Gatling plugin for Maven - Java demo project
============================================

## Socket.io connection

**IMPORTANT:**
Requires in the connect string: 

```java
.connect("/socket.io/?EIO=4&transport=websocket")
```

## Configuration file

In file: `src/test/resources/gatling.conf`



## Rest

A simple showcase of a Maven project using the Gatling plugin for Maven. Refer to the plugin documentation
[on the Gatling website](https://gatling.io/docs/current/extensions/maven_plugin/) for usage.

This project is written in Java, others are available for [Kotlin](https://github.com/gatling/gatling-maven-plugin-demo-kotlin)
and [Scala](https://github.com/gatling/gatling-maven-plugin-demo-scala).

It includes:

* [Maven Wrapper](https://maven.apache.org/wrapper/), so that you can immediately run Maven with `./mvnw` without having
  to install it on your computer
* minimal `pom.xml`
* latest version of `io.gatling:gatling-maven-plugin` applied
* sample [Simulation](https://gatling.io/docs/gatling/reference/current/general/concepts/#simulation) class,
  demonstrating sufficient Gatling functionality
* proper source file layout


## Run

### Run simulations.
```
mvn gatling:test
```

### Run the Gatling Recorder
```
mvn galing:recorder
```
The recorder does not capture websocket events.