// junit test class for SocketIOHelper.java
package socketio;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextFrameTest {

  @Test
  public void when_no_namespace_eventFrame_includes_only_event_and_data() {
    String actual = TextFrame.eventFrame("event", "data", "");
    String expected = "42[\"event\",\"data\"]";
    assertEquals(expected, actual);
  }

  @Test
  public void when_a_namespace_eventFrame_includes_the_namespace() {
    String actual = TextFrame.eventFrame("event", "data", "namespace");
    String expected = "42/namespace,[\"event\",\"data\"]";
    assertEquals(expected, actual);
  }

  @Test
  public void when_no_namespace_connectFrame_includes_only_connect() {
    String actual = TextFrame.connectFrame("");
    String expected = "40";
    assertEquals(expected, actual);
  }

  @Test
  public void when_a_namespace_connectFrame_includes_the_namespace() {
    String actual = TextFrame.connectFrame("namespace");
    String expected = "40/namespace";
    assertEquals(expected, actual);
  }

  @Test
  public void when_no_namespace_disconnectFrame_includes_only_disconnect() {
    String actual = TextFrame.disconnectFrame("");
    String expected = "41";
    assertEquals(expected, actual);
  }

  @Test
  public void when_a_namespace_disconnectFrame_includes_the_namespace() {
    String actual = TextFrame.disconnectFrame("namespace");
    String expected = "41/namespace";
    assertEquals(expected, actual);
  }

}
