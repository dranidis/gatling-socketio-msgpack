package socketio;

import io.gatling.javaapi.http.WsAwaitActionBuilder;
import io.gatling.javaapi.core.Session;
import java.util.function.Function;
import javax.annotation.Nonnull;

public interface SocketIOProtocol {

  WsAwaitActionBuilder<?, ?> send(SocketIOPacket packet);

  WsAwaitActionBuilder<?, ?> send(@Nonnull Function<Session, SocketIOPacket> sessionFunction);
}
