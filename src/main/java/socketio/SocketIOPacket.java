package socketio;

import java.util.List;

// import com.fasterxml.jackson.annotation.JsonFilter;

// @JsonFilter("filterData")
class SocketIOPacket {
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
}
