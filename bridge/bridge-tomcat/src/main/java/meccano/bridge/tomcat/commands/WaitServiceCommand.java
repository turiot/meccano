package meccano.bridge.tomcat.commands;

import java.util.ArrayList;
import java.util.logging.Level;
import meccano.bridge.core.ServiceRegistry;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Asynchronous waiting for needed services.
 *
 * @author Thierry Uriot
 */
public class WaitServiceCommand extends WaitCommand {

  /** the global service registry. */
  private ServiceRegistry sr;
  /** a copy of need services. */
  private ArrayList<String> listServices;

  /**
   * take the list of needed services.
   * @return success
   */
  @Override
  @SuppressWarnings("unchecked")
  protected boolean init() {
    sr = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;
    final ArrayList<String> neededServices = (ArrayList<String>) context.getAttribute(NEEDED_SERVICES);
    listServices = new ArrayList<String>(neededServices.size());
    for (final String service : neededServices) {
      LOG.log(Level.INFO, "------- need service {0}", service);
      listServices.add(service);
    }
    return true;
  }

  /**
   * testing presence of service in the global registry, can be called many times.
   * @return success
   */
  @Override
  protected boolean test() {
    boolean ready = false;

    int size = listServices.size();
    for (int i = 0; i < size; i++) {
      final String service = listServices.get(i);
      final Object s = sr.lookup(service);
      if (s != null) {
        LOG.log(Level.INFO, "------- got service {0}", service);
        listServices.remove(listServices.get(i));
        i--;
        size--;
      }
    }

    if (size == 0) {
      ready = true;
    }

    return ready;
  }

  /**
   * in case of success.
   */
  @Override
  protected void ok() {
    context.setAttribute(SERVICE_READY, true);
  }

  /**
   * in case of failure.
   */
  @Override
  protected void ko() {
    // nothing to do.
  }

}
