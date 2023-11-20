package socketio.protocols;

import java.util.function.Function;

import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.Ws;
import io.gatling.javaapi.http.WsAwaitActionBuilder;
import socketio.SocketIOPacket;
import socketio.SocketIOProtocol;

public class MsgPackSocketIOProtocol implements SocketIOProtocol {

  private Ws websocket;
  private Parser<byte[]> parser = new MessagePackParser();

  public MsgPackSocketIOProtocol(Ws websocket) {
    this.websocket = websocket;
  }

  @Override
  public WsAwaitActionBuilder send(SocketIOPacket packet) {
    return this.websocket.sendBytes(parser.encode(packet));

  }

  @Override
  public WsAwaitActionBuilder send(Function<Session, SocketIOPacket> sessionFunction) {
    return this.websocket.sendBytes(session -> {
      SocketIOPacket packet = sessionFunction.apply(session);
      return parser.encode(packet);
    });
  }

}
