package socketio.protocols;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

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

  protected String dataToString(List<Object> data) {

    List<String> strings = new ArrayList<String>();

    for (Object o : data) {
      if (o instanceof String) {

        String s = (String) o;
        strings.add("\"" + s + "\"");

      } else if (o instanceof Map<?, ?>) {

        @SuppressWarnings("unchecked")
        String s = convertMapToJson((Map<String, Object>) o);
        strings.add(s);

      } else {
        throw new RuntimeException("dataToString: " + o.getClass().getName());
      }
    }

    return strings.stream().collect(Collectors.joining(",", "[", "]"));
  }

  private String convertMapToJson(Map<String, Object> dataMap) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();

      return objectMapper.writeValueAsString(dataMap);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
