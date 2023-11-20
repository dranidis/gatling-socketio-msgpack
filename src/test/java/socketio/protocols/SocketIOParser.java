package socketio.protocols;

import socketio.SocketIOPacket;

public interface SocketIOParser<T> {
  T encode(SocketIOPacket packet);

  String packetType();
}
