package socketio;

import io.gatling.javaapi.http.WsAwaitActionBuilder;

public interface SocketIOProtocol {
  WsAwaitActionBuilder send(SocketIOPacket packet);
}
