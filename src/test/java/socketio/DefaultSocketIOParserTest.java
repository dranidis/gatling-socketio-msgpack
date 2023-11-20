// junit test class for SocketIOHelper.java
package socketio;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import socketio.protocols.DefaultSocketIOParser;

public class DefaultSocketIOParserTest {

  DefaultSocketIOParser parser = new DefaultSocketIOParser();

  @Test
  public void when_no_namespace_eventFrame_includes_only_event_and_data() {
    SocketIOPacket packet = new SocketIOPacketBuilder(2)
        .addData("event")
        .addData("data")
        .build();
    String expected = "42[\"event\",\"data\"]";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_a_namespace_eventFrame_includes_the_namespace() {
    SocketIOPacket packet = new SocketIOPacketBuilder(2)
        .nsp("namespace")
        .addData("event")
        .addData("data")
        .build();
    String expected = "42/namespace,[\"event\",\"data\"]";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_a_namespace_starts_with_slash_eventFrame_includes_the_namespace() {
    SocketIOPacket packet = new SocketIOPacketBuilder(2)
        .nsp("/namespace")
        .addData("event")
        .addData("data")
        .build();
    String expected = "42/namespace,[\"event\",\"data\"]";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_no_namespace_connectFrame_includes_only_connect() {
    SocketIOPacket packet = new SocketIOPacketBuilder(0).build();
    String expected = "40";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_a_namespace_connectFrame_includes_the_namespace() {
    SocketIOPacket packet = new SocketIOPacketBuilder(0)
        .nsp("namespace")
        .build();
    String expected = "40/namespace";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_a_namespace_with_slash_connectFrame_includes_the_namespace() {
    SocketIOPacket packet = new SocketIOPacketBuilder(0)
        .nsp("/namespace")
        .build();
    String expected = "40/namespace";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_no_namespace_disconnectFrame_includes_only_disconnect() {
    SocketIOPacket packet = new SocketIOPacketBuilder(1).build();
    String expected = "41";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_a_namespace_disconnectFrame_includes_the_namespace() {
    SocketIOPacket packet = new SocketIOPacketBuilder(1)
        .nsp("namespace")
        .build();
    String expected = "41/namespace";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_a_frame_has_3_arguments() {
    SocketIOPacket packet = new SocketIOPacketBuilder(2)
        .nsp("namespace")
        .addData("event")
        .addData("data")
        .addData("more data")
        .build();
    String expected = "42/namespace,[\"event\",\"data\",\"more data\"]";
    assertEquals(expected, parser.encode(packet));
  }

  @Test
  public void when_a_frame_has_a_json_it_does_not_quote_the_json() {
    SocketIOPacket packet = new SocketIOPacketBuilder(2)
        .nsp("namespace")
        .addData("event")
        .addData("data")
        .addData("{\"id\": \"42\"}")
        .build();
    String expected = "42/namespace,[\"event\",\"data\",{\"id\": \"42\"}]";
    assertEquals(expected, parser.encode(packet));
  }

}
