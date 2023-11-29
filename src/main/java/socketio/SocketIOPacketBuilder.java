package socketio;

import java.util.ArrayList;

public class SocketIOPacketBuilder {
  private SocketIOPacket packet;

  public SocketIOPacketBuilder(int type) {
    this.packet = new SocketIOPacket(type, "/", new ArrayList<>());
  }

  public SocketIOPacketBuilder addData(Object data) {
    this.packet.getData().add(data);
    return this;
  }

  public SocketIOPacketBuilder nsp(String nsp) {
    this.packet.setNsp(nsp);
    return this;
  }

  public SocketIOPacket build() {
    return this.packet;
  }

}
