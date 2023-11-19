package socketio;

import io.gatling.javaapi.http.Ws;

public interface SocketIOProtocolFactory {
  public SocketIOProtocol createSocketIOProtocol(Ws webSocket);
}
