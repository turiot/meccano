package meccano.bridge.tomcat.commands;

import java.util.ArrayList;
import java.util.logging.Level;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Asynchronous launching of listerners.
 *
 * @author Thierry Uriot
 */
public class RunListenersCommand extends Command {

  /**
   * Instantiate custom listeners and sequentially launch their contextInitialized method.
   * @return success
   */
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public boolean run() {
    boolean success = false;
    try {
      final ArrayList<String> customListeners = (ArrayList<String>) context.getAttribute(CUSTOM_LISTENERS);
      final ServletContextEvent sce = new ServletContextEvent(context);
      for (final String listener : customListeners) {
        LOG.log(Level.INFO, "------- run listener {0} ", listener);
        final Class cls = Class.forName(listener);
        final ServletContextListener instance = (ServletContextListener) cls.newInstance();
        instance.contextInitialized(sce);
      }
      success = true;
    } catch (ClassNotFoundException e) {
      LOG.log(Level.SEVERE, "------- error {0}", e);
    } catch (InstantiationException e) {
      LOG.log(Level.SEVERE, "------- error {0}", e);
    } catch (IllegalAccessException e) {
      LOG.log(Level.SEVERE, "------- error {0}", e);
    }
    return success;
  }

}
