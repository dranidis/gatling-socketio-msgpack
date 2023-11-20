package socketio.protocols;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import socketio.SocketIODisconnectPacket;
import socketio.SocketIOPacket;
import socketio.SocketIOType;

public class MessagePackParser implements Parser<byte[]> {
  private ObjectMapper objectMapper;

  public MessagePackParser() {
    objectMapper = new ObjectMapper(new MessagePackFactory());
  }

  @Override
  public byte[] encode(SocketIOPacket packet) {
    try {
      if (packet.type == SocketIOType.DISCONNECT.getValue()) {
        return objectMapper.writeValueAsBytes(
            new SocketIODisconnectPacket(packet.type, packet.nsp));
      } else {
        return objectMapper.writeValueAsBytes(packet);
      }

    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Invalid JSON packet");

    }
  }
}
