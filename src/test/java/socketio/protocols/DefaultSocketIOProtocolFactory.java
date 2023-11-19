package socketio.protocols;

import io.gatling.javaapi.http.Ws;
import socketio.SocketIOProtocol;
import socketio.SocketIOProtocolFactory;

public class DefaultSocketIOProtocolFactory implements SocketIOProtocolFactory {

  @Override
  public SocketIOProtocol createSocketIOProtocol(Ws webSocket) {
    return new DefaultSocketIOProtocol(webSocket);
  }

}
