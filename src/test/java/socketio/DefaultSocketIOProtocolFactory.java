package socketio;

import io.gatling.javaapi.http.Ws;

public class DefaultSocketIOProtocolFactory implements SocketIOProtocolFactory {

  @Override
  public SocketIOProtocol createSocketIOProtocol(Ws webSocket) {
    return new DefaultSocketIOProtocol(webSocket);
  }

}
