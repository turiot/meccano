package meccano.bridge.tomcat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import meccano.bridge.core.ServiceRegistry;
import static meccano.bridge.tomcat.Constants.*;
import meccano.bridge.tomcat.util.DecoratedLogger;

/**
 * Context listener launching commands in a thread.
 *
 * @author Thierry Uriot
 */
public class AsyncContextListener implements ServletContextListener {

  /**
   * logger.
   */
  private static final DecoratedLogger LOG = DecoratedLogger.getLogger(AsyncContextListener.class.getName());

  /**
   * application initialization.
   *
   * @param event event
   */
  public void contextInitialized(final ServletContextEvent event) {
    final ServletContext context = event.getServletContext();
    LOG.contextName = context.getServletContextName();
    context.setAttribute(APP_STATUS, STATUS_INIT);
    LOG.log(Level.INFO, "------- starting");
    boolean clok = false;
    final ClassLoader tcl = this.getClass().getClassLoader();
    final CompositeLoader compo = new CompositeLoader(tcl.getParent());
    context.setAttribute(CLASSLOADER, compo);
    try {
      final Field[] decfields = tcl.getClass().getDeclaredFields();
      for (final Field field : decfields) {
        if ("parent".equals(field.getName())) {
          field.setAccessible(true);
          field.set(tcl, compo);
        }
      }
      clok = true;
    }
    catch (SecurityException e) { // no multicatch for 1.5 compatibility
      LOG.log(Level.SEVERE, "exception", e);
    }
    catch (IllegalArgumentException e) {
      LOG.log(Level.SEVERE, "exception", e);
    }
    catch (IllegalAccessException e) {
      LOG.log(Level.SEVERE, "exception", e);
    }
    if (clok) {
      final ExecutorService executor = Executors.newSingleThreadExecutor();
      context.setAttribute(EXECUTOR, executor);
      final Runnable runnable = new CommandController(context);
      executor.execute(runnable);
      executor.shutdown();
    }
  }

  /**
   * application end.
   *
   * @param event event
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void contextDestroyed(final ServletContextEvent event) {
    final ServletContext context = event.getServletContext();
    final ArrayList<String> customListeners = (ArrayList<String>) context.getAttribute(CUSTOM_LISTENERS);
    if (customListeners != null) {
      try {
        final ServletContextEvent sce = new ServletContextEvent(context);
        for (final String listener : customListeners) {
          LOG.log(Level.INFO, "------- run end listener {0} ", listener);
          final Class cls = Class.forName(listener);
          final ServletContextListener instance = (ServletContextListener) cls.newInstance();
          instance.contextDestroyed(sce);
        }
      } catch (ClassNotFoundException e) {
        LOG.log(Level.SEVERE, "------- error {0}", e);
      } catch (InstantiationException e) {
        LOG.log(Level.SEVERE, "------- error {0}", e);
      } catch (IllegalAccessException e) {
        LOG.log(Level.SEVERE, "------- error {0}", e);
      }
    }
    
    final ArrayList<String> givenServices = (ArrayList<String>) context.getAttribute(GIVEN_SERVICES);
    if (givenServices != null) {
      final ServiceRegistry registry = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;
      for (String service : givenServices) {
        registry.unbind(service);
        LOG.log(Level.INFO, "service {0} unregistered", service);
      }
    }

    final ExecutorService executor = (ExecutorService) context.getAttribute(EXECUTOR);
    if (executor != null) {
      executor.shutdownNow();
    }
  }
}