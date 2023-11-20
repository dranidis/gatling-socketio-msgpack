package socketio;

import java.util.List;

public class SocketIOPacket {
  // TODO: change type to SocketIOType
  private int type;
  private String nsp;
  private List<String> data;

  public SocketIOPacket() {
  }

  public SocketIOPacket(int type, String nsp, List<String> data) {
    this.type = type;
    this.nsp = nsp;
    this.data = data;
  }

  public int getType() {
    return type;
  }

  public String getNsp() {
    return nsp;
  }

  public List<String> getData() {
    return data;
  }

  public void setNsp(String nsp) {
    this.nsp = nsp;
  }

}
