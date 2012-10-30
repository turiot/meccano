package meccano.test3.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import meccano.bridge.core.ServiceRegistry;
import meccano.test3.api.IService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Custom listener (called asynchronously).
 */
public class InstantiateListener implements ServletContextListener {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(InstantiateListener.class.getName());

  /**
   * Service container initialization.
   * Creates a service instance via Spring and registers it.
   *
   * @param event event
   */
  public void contextInitialized(final ServletContextEvent event) {
    LOG.log(Level.INFO, "------- starting applicaton");
    final ServiceRegistry sr = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;

    ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
    IService service = (IService) ctx.getBean("service");
    sr.bind("test3.service", service);
  }

  /**
   * Service container shutdown.
   * Unregisters service.
   *
   * @param event event
   */
  public void contextDestroyed(final ServletContextEvent event) {
    LOG.log(Level.INFO, "------- ending application");
    final ServiceRegistry sr = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;
    sr.unbind("test3.service");
  }
}