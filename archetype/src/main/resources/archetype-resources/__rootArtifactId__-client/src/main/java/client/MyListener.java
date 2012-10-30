#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import ${groupId}.bridge.core.ServiceRegistry;
import ${package}.api.IService;
import static ${groupId}.bridge.tomcat.Constants.*;

/**
 * Custom listener (called asynchronously).
 */
public class MyListener implements ServletContextListener {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(MyListener.class.getName());

  /**
   * application initialization.
   *
   * @param event event
   */
  public void contextInitialized(final ServletContextEvent event) {
    LOG.log(Level.INFO, "------- starting applicaton");
    final ServiceRegistry sr = ServiceRegistry.getInstance();
    String serviceName = "${parentArtifactId}.service";
    final Object s = sr.lookup(serviceName);
    final IService service = (IService) s;
    LOG.log(Level.INFO, "===> {0}", service.test());
  }

  /**
   * application end.
   *
   * @param event event
   */
  public void contextDestroyed(final ServletContextEvent event) {
    LOG.log(Level.INFO, "------- ending application");
  }
}