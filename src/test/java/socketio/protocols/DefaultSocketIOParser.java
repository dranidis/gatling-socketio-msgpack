package socketio.protocols;

import socketio.SocketIOPacket;
import socketio.SocketIOType;
import socketio.TextFrame;

public class DefaultSocketIOParser implements SocketIOParser<String> {

  @Override
  public String encode(SocketIOPacket packet) {
    String textFrame = "";
    switch (SocketIOType.fromValue(packet.type)) {
      case CONNECT:
        textFrame = TextFrame.getInstance()
            .connectFrame(packet.nsp);
        break;
      case DISCONNECT:
        textFrame = TextFrame.getInstance()
            .disconnectFrame(packet.nsp);
        break;
      case EVENT:
        textFrame = TextFrame.getInstance()
            .eventFrame(packet.nsp, packet.data.toArray(new String[0]));
        break;
      // case 3:
      // textFrame = TextFrame.getInstance().ackFrame(packet);
      // case 4:
      // textFrame = TextFrame.getInstance().errorFrame(packet);
      // case 5:
      // textFrame = TextFrame.getInstance().binaryEventFrame(packet);
      // case 6:
      // textFrame = TextFrame.getInstance().binaryAckFrame(packet);
      default:
        throw new IllegalArgumentException("Invalid packet type");
    }
    return textFrame;
  }

  @Override
  public String packetType() {
    return "text";
  }

}
