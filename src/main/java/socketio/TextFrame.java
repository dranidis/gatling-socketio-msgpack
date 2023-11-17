package socketio;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextFrame implements Packet<String> {

  private TextFrame() {
  }

  public static TextFrame getInstance() {
    return new TextFrame();
  }

  /*
   * PRIVATE METHODS
   */

  /*
   * 4 => the Engine.IO message type
   * 0 => the Socket.IO CONNECT type
   * 1 => the Socket.IO DISCONNECT type
   * 2 => the Socket.IO EVENT type
   */
  private static final String CONNECT_FRAME = "40";
  private static final String DISCONNECT_FRAME = "41";
  private static final String EVENT_FRAME = "42";

  /**
   * The default namespace is "/". "" can also be used to represent the default
   * 
   * @param nameSpace
   * @return
   */
  private static boolean notDefaultNamespace(String nameSpace) {
    return !nameSpace.equals("/") && !nameSpace.equals("");
  }

  private static String textFrame(String text, String nameSpace) {
    String frame = text;

    if (notDefaultNamespace(nameSpace)) {
      frame += (nameSpace.startsWith("/") ? "" : "/") + nameSpace;

      if (text.equals(EVENT_FRAME)) {
        frame += ",";
      }
    }

    return frame;
  }

  @Override
  public String eventFrame(String nameSpace, String... arg) {
    return textFrame(EVENT_FRAME, nameSpace) +
        Arrays
            // json objects should not get quoted
            .stream(arg).map(s -> s.startsWith("{") ? s : "\"" + s + "\"")
            .collect(Collectors.joining(",", "[", "]"));
  }

  @Override
  public String connectFrame(String nameSpace) {
    return textFrame(CONNECT_FRAME, nameSpace);
  }

  @Override
  public String disconnectFrame(String nameSpace) {
    return textFrame(DISCONNECT_FRAME, nameSpace);
  }

}
