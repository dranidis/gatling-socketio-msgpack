package socketio;

import io.gatling.javaapi.http.Ws;
import io.gatling.javaapi.http.WsAwaitActionBuilder;

public class DefaultSocketIOProtocol implements SocketIOProtocol {
  private Ws websocket;

  public DefaultSocketIOProtocol(Ws websocket) {
    this.websocket = websocket;
  }

  @Override
  public WsAwaitActionBuilder send(SocketIOPacket packet) {
    return websocket.sendText(TextFrame.getInstance().connectFrame(packet.nsp));

  }

}
