package socketio;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

public class MsgpackFrame implements Packet<byte[]> {

  @Override
  public byte[] eventFrame(String namespace, String... arg) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'eventFrame'");
  }

  @Override
  public byte[] connectFrame(String namespace) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'connectFrame'");
  }

  @Override
  public byte[] disconnectFrame(String namespace) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'disconnectFrame'");
  }

  //   MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
  // try {
  //   packer.packString(frame);
  // } catch (Exception e) {
  //   e.printStackTrace();
  // }

  // return packer.toByteArray().toString();

}
