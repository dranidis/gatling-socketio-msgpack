package socketio;

public class SocketIODisconnectPacket {
  public int type;
  public String nsp;

  public SocketIODisconnectPacket() {
  }

  public SocketIODisconnectPacket(int type, String nsp) {
    this.type = type;
    this.nsp = nsp;
  }
}
