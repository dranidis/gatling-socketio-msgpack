package socketio;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static socketio.SocketIOHelper.*;

import java.util.List;
import java.util.Map;


import io.gatling.core.Predef.*;
import io.gatling.http.Predef.*;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.jsonFile;

import java.util.List;
import java.util.Map;

public class TestFeeder extends Simulation {
  public static void a(String[] args) {
    // TODO Auto-generated method stub
    List<Map<String, Object>> records = jsonFile("data.json").readRecords();

    for (Map<String, Object> record : records) {
      System.out.println(record);
    }

  }

  public static void main(String[] args) {
    GatlingSimulation gatlingSimulation = new GatlingSimulation();

  }
}
