package socketio.protocols;

import java.util.List;
import java.util.stream.Collectors;

import socketio.SocketIOPacket;

public class DefaultSocketIOParser implements SocketIOParser<String> {

  private static final String VERSION = "4";

  @Override
  public String encode(SocketIOPacket packet) {
    String nameSpace = packet.getNsp();
    List<String> data = packet.getData();

    StringBuffer textFrame = new StringBuffer()
        .append(VERSION)
        .append(packet.getType());

    if (notDefaultNamespace(nameSpace)) {
      textFrame.append(
          nameSpace.startsWith("/") ? "" : "/");
      textFrame.append(nameSpace);

      if (!data.isEmpty()) {
        textFrame.append(",");
      }
    }

    if (!data.isEmpty()) {
      textFrame.append(dataToString(data));
    }

    return textFrame.toString();
  }

  @Override
  public String packetType() {
    return "text";
  }

  /**
   * The default namespace is "/". "" can also be used to represent the default
   * 
   * @param nameSpace
   * @return
   */
  protected boolean notDefaultNamespace(String nameSpace) {
    return !nameSpace.equals("/") && !nameSpace.equals("");
  }

  protected String dataToString(List<String> data) {
    return data.stream().map(s -> s.startsWith("{") ? s : "\"" + s + "\"")
        .collect(Collectors.joining(",", "[", "]"));
  }

}
