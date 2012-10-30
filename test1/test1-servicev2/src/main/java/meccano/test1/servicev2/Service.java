package meccano.test1.servicev2;

import java.util.logging.Logger;
import meccano.test1.api.IService;
import meccano.test1.api.Model1;

/**
 * Service implementation.
 */
public class Service implements IService {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(Service.class.getName());

  /**
   * method implementation.
   *
   * @return String
   */
  public String sayHelloFromBean() {
    return "ok";
  }

  /**
   * method implementation.
   *
   * @return Model1
   */
  public Model1 getValue() {
    LOG.info("service v2 called");
    final Model1 value = new Model1();
    value.setAttribut("value v2");
    return value;
  }
}
