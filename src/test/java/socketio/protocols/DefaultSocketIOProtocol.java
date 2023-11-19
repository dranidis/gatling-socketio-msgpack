package socketio.protocols;

import java.util.function.Function;

import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.Ws;
import io.gatling.javaapi.http.WsAwaitActionBuilder;
import socketio.SocketIOPacket;
import socketio.SocketIOProtocol;
import socketio.TextFrame;

public class DefaultSocketIOProtocol implements SocketIOProtocol {
  private Ws websocket;

  public DefaultSocketIOProtocol(Ws websocket) {
    this.websocket = websocket;
  }

  @Override
  public WsAwaitActionBuilder send(SocketIOPacket packet) {
    return this.websocket.sendText(socketIOPacketToString(packet));

  }

  @Override
  public WsAwaitActionBuilder send(Function<Session, SocketIOPacket> sessionFunction) {
    return this.websocket.sendText(session -> {
      SocketIOPacket packet = sessionFunction.apply(session);
      return this.socketIOPacketToString(packet);
    });
  }

  private String socketIOPacketToString(SocketIOPacket packet) {
    String textFrame = "";
    switch (packet.type) {
    case 0:
      textFrame = TextFrame.getInstance()
          .connectFrame(packet.nsp);
      break;
    case 1:
      textFrame = TextFrame.getInstance()
          .disconnectFrame(packet.nsp);
      break;
    case 2:
      textFrame = TextFrame.getInstance()
          .eventFrame(packet.nsp, packet.data.toArray(new String[0]));
      break;
    // case 3:
    //   textFrame = TextFrame.getInstance().ackFrame(packet);
    // case 4:
    //   textFrame = TextFrame.getInstance().errorFrame(packet);
    // case 5:
    //   textFrame = TextFrame.getInstance().binaryEventFrame(packet);
    // case 6:
    //   textFrame = TextFrame.getInstance().binaryAckFrame(packet);
    default:
      throw new IllegalArgumentException("Invalid packet type");
    }
    return textFrame;
  }

}
