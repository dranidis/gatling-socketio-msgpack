package socketio.protocols;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import socketio.SocketIOPacket;

public class DefaultSocketIOParser implements SocketIOParser<String> {

  private static final String VERSION = "4";

  @Override
  public String encode(SocketIOPacket packet) {
    String nameSpace = packet.getNsp();
    List<Object> data = packet.getData();

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

  // TODO: test to send with a text protocol,
  // a JSON object to a socket.io server
  protected String dataToString(List<Object> data) {

    Stream<String> stringStream = data.stream().filter(o -> o instanceof String).map(o -> (String) o)
        .map(s -> s.startsWith("{") ? s : "\"" + s + "\"");

    return stringStream.collect(Collectors.joining(",", "[", "]"));
  }

}
