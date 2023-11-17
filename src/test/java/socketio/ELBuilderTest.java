// junit test class for SocketIOHelper.java
package socketio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ELBuilderTest {

  @Test
  public void check_if_EL_expression() {
    String expression = "#{something}";
    boolean actual = ELBuilder.isEL(expression);
    assertTrue(actual);
  }

  @Test
  public void check_if_not_EL_expression() {
    String expression = "something";
    boolean actual = ELBuilder.isEL(expression);
    assertTrue(!actual);
  }

  @Test
  public void test_builder() {
    String actual = ELBuilder.el("#{something}")
        .getAttribute()
        .toString();
    String expected = "#{something}";
    assertEquals(expected, actual);
  }

  @Test
  public void test_builder_size() {
    String actual = ELBuilder.el("#{something}")
        .getAttribute()
        .append(".size()")
        .toString();
    String expected = "#{something.size()}";
    assertEquals(expected, actual);
  }

}
