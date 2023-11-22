package socketio.protocols;

import java.util.function.Function;

import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.Ws;
import io.gatling.javaapi.http.WsAwaitActionBuilder;
import socketio.SocketIOPacket;
import socketio.SocketIOProtocol;

public class UnifiedSocketIOProtocol<T> implements SocketIOProtocol {

  private Ws websocket;
  private SocketIOParser<T> parser;

  public UnifiedSocketIOProtocol(Ws websocket, SocketIOParser<T> parser) {
    this.websocket = websocket;
    this.parser = parser;
  }

  @Override
  public WsAwaitActionBuilder<?, ?> send(SocketIOPacket packet) {
    if (parser.packetType().equals("binary")) {
      return this.websocket.sendBytes((byte[]) parser.encode(packet));
    } else {
      return this.websocket.sendText((String) parser.encode(packet));
    }

  }

  @Override
  public WsAwaitActionBuilder<?, ?> send(Function<Session, SocketIOPacket> sessionFunction) {
    if (parser.packetType().equals("binary")) {
      return this.websocket.sendBytes(session -> {
        SocketIOPacket packet = sessionFunction.apply(session);
        return (byte[]) parser.encode(packet);
      });
    } else {
      return this.websocket.sendText(session -> {
        SocketIOPacket packet = sessionFunction.apply(session);
        return (String) parser.encode(packet);
      });
    }

  }

}
