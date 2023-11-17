package socketio;

/**
 * DSL for manipulating EL expressions
 * https://gatling.io/docs/gatling/reference/current/core/session/el/
 */
public class ELBuilder {

  private String attribute;

  public static boolean isEL(String attribute) {
    return attribute.startsWith("#{") && attribute.endsWith("}");
  }

  public ELBuilder(String attribute) {
    this.attribute = attribute;
  }

  /**
   * Bootstrap the builder
   * 
   * @param attribute
   * @return
   */
  public static ELBuilder el(String attribute) {
    return new ELBuilder(attribute);
  }

  public ELBuilder getAttribute() {
    attribute = attribute.substring(2, attribute.length() - 1);
    return this;
  }

  public ELBuilder append(String string) {
    attribute += string;
    return this;
  }

  public String toString() {
    return "#{" + attribute + "}";
  }

}
