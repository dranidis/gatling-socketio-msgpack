package socketio;

public interface PacketFrame<T> {

  T eventFrame(String namespace, String... arg);

  T connectFrame(String namespace);

  T disconnectFrame(String namespace);

}
