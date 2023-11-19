package socketio;

public enum SocketIOType {
  CONNECT(0),
  DISCONNECT(1),
  EVENT(2),
  ACK(3),
  ERROR(4),
  BINARY_EVENT(5),
  BINARY_ACK(6);

  private int value;

  private SocketIOType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static SocketIOType fromValue(int value) {
    for (SocketIOType type : SocketIOType.values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid SocketIOType value");
  }

}
