package socketio;

import java.util.ArrayList;
import java.util.List;

public class SocketIOPacket {
  // TODO: make private
  // TODO: change type to SocketIOType
  public int type;
  public String nsp;
  public List<String> data;

  public SocketIOPacket() {
  }

  public SocketIOPacket(int type, String nsp, List<String> data) {
    this.type = type;
    this.nsp = nsp;
    this.data = data;
  }

  // DSL for SocketIOPacket creation

  public static SocketIOPacket packet(int type) {
    return new SocketIOPacket(type, "/", new ArrayList<>());
  }

  public SocketIOPacket addData(String data) {
    this.data.add(data);
    return this;
  }

  public SocketIOPacket nsp(String nsp) {
    this.nsp = nsp;
    return this;
  }
}
