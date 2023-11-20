package socketio.protocols;

import socketio.SocketIOPacket;

public interface Parser<T> {
  T encode(SocketIOPacket packet);
}
