#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import ${package}.api.IService;

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
  public String test() {
    LOG.info("service called");
    
    
    return "ok";
  }
}
