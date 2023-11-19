package socketio.protocols;

import java.util.function.Function;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.Ws;
import io.gatling.javaapi.http.WsAwaitActionBuilder;
import socketio.SocketIOPacket;
import socketio.SocketIOProtocol;

public class MsgPackSocketIOProtocol implements SocketIOProtocol {
  private Ws websocket;
  ObjectMapper objectMapper;

  public MsgPackSocketIOProtocol(Ws websocket) {
    this.websocket = websocket;
    objectMapper = new ObjectMapper(new MessagePackFactory());
  }

  @Override
  public WsAwaitActionBuilder send(SocketIOPacket packet) {
    return this.websocket.sendBytes(socketIOPacketToString(packet));

  }

  @Override
  public WsAwaitActionBuilder send(Function<Session, SocketIOPacket> sessionFunction) {
    return this.websocket.sendBytes(session -> {
      SocketIOPacket packet = sessionFunction.apply(session);
      return this.socketIOPacketToString(packet);
    });
  }

  private byte[] socketIOPacketToString(SocketIOPacket packet) {
    try {
      return objectMapper.writeValueAsBytes(packet);
    } catch (JsonProcessingException e) {
      e.printStackTrace();

      // TODO: handle exception??

      return new byte[0];

    }
  }
}
