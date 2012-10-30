package meccano.bridge.tomcat.commands;

import java.util.ArrayList;
import java.util.logging.Level;
import meccano.bridge.core.ServiceRegistry;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Asynchronous service registring.
 *
 * @author Thierry Uriot
 */
public class SetServiceCommand extends Command {

  /**
   * For parametrized classes, add a new instance in the global service registry.
   * @return succes
   */
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public boolean run() {
    boolean success = false;
    final ServiceRegistry registry = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;
    try {
      final ArrayList<String> givenServices = (ArrayList<String>) context.getAttribute(GIVEN_SERVICES);
      final ArrayList<String> givenClasses = (ArrayList<String>) context.getAttribute(GIVEN_CLASSES);
      final int size = givenServices.size();
      for (int i = 0; i < size; i++) {
        final String service = givenServices.get(i);
        final String classname = givenClasses.get(i);
        final Class cls = Class.forName(classname);
        final Object instance = cls.newInstance();
        registry.bind(service, instance);
        LOG.log(Level.INFO, "------- added service {0} ({1})", new String[]{service, classname});
      }
      context.setAttribute(SETSERVICE_READY, true);
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
