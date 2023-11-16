package socketio;

public class TextFrame {

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

  protected static String textFrame(String text, String nameSpace) {
    String frame = text;

    if (notDefaultNamespace(nameSpace)) {
      frame += (nameSpace.startsWith("/") ? "" : "/") + nameSpace;

      if (text.equals(EVENT_FRAME)) {
        frame += ",";
      }
    }

    return frame;
  }

  protected static String eventFrame(String eventName, String message, String nameSpace) {
    return textFrame(EVENT_FRAME, nameSpace)
        + "[\"" + eventName + "\",\"" + message + "\"]";
  }

  protected static String connectFrame(String nameSpace) {
    return textFrame(CONNECT_FRAME, nameSpace);
  }

  protected static String disconnectFrame(String nameSpace) {
    return textFrame(DISCONNECT_FRAME, nameSpace);
  }

  public static boolean isEL(String expression) {
    return expression.startsWith("#{") && expression.endsWith("}");
  }

  public static String getStringFromEL(String expression) {
    return expression.substring(2, expression.length() - 1);
  }

}
