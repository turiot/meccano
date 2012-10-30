package meccano.test1.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import meccano.bridge.core.ServiceRegistry;
import meccano.test1.api.IService;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Custom listener (called asynchronously) using a service from another war.
 */
public class ClientListener implements ServletContextListener {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(ClientListener.class.getName());

  /**
   * application initialization.
   *
   * @param event event
   */
  public void contextInitialized(final ServletContextEvent event) {
    LOG.log(Level.INFO, "------- starting client");
    final ServiceRegistry sr = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;
    String serviceName = "service-1.0";
    final Object s = sr.lookup(serviceName);
    if (s == null) {
      LOG.log(Level.SEVERE, "service '{0}' not found in registry", serviceName);
      event.getServletContext().setAttribute(APP_STATUS, STATUS_FAILED);
    }
    else {
      final IService service = (IService) s;
      LOG.log(Level.INFO, "===> {0}", service.sayHelloFromBean());
      LOG.log(Level.INFO, "===> {0}", service.getValue().getAttribut());
    }
  }

  /**
   * application end.
   *
   * @param event event
   */
  public void contextDestroyed(final ServletContextEvent event) {
    LOG.log(Level.INFO, "------- ending client");
  }
}