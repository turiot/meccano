package meccano.test3.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import meccano.test3.api.IService;

/**
 * Service implementation.
 */
public class Service implements IService {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(Service.class.getName());

  /**
   * A property.
   */
  private String userName;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  /**
   * Method implementation.
   *
   * @return String
   */
  public String test() {
    LOG.log(Level.INFO, "service called by {0}", userName);
    return userName;
  }
}
