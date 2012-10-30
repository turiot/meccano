package meccano.bridge.tomcat.commands;

import java.util.ArrayList;
import java.util.logging.Level;
import meccano.bridge.core.ApiRegistry;
import meccano.bridge.tomcat.CompositeLoader;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Asynchronous waiting for needed api's.
 *
 * @author Thierry Uriot
 */
public class WaitApiCommand extends WaitCommand {

  /** the global api registry. */
  private ApiRegistry ar;
  /** a copy of need apis. */
  private ArrayList<String> listApis;

  /**
   * take the list of needed apis.
   * @return success
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean init() {
    ar = ApiRegistry.Singleton.INSTANCE.apiRegistry;
    final ArrayList<String> neededApis = (ArrayList<String>) context.getAttribute(NEEDED_APIS);
    listApis = new ArrayList<String>(neededApis.size());
    for (final String api : neededApis) {
      LOG.log(Level.INFO, "------- need api {0}", api);
      listApis.add(api);
    }
    return true;
  }

  /**
   * testing presence of api in the global registry, can be called many times.
   * @return success
   */
  @Override
  protected boolean test() {
    boolean ready = false;

    int size = listApis.size();
    for (int i = 0; i < size; i++) {
      final String api = listApis.get(i);
      final ClassLoader apiClassLoader = ar.getClassLoaderForName(api);
      if (apiClassLoader != null) {
        LOG.log(Level.INFO, "------- got cl for {0}", api);
        final CompositeLoader compositeClassLoader = (CompositeLoader) context.getAttribute(CLASSLOADER);
        compositeClassLoader.addClassLoader(apiClassLoader);
        LOG.log(Level.INFO, "------- added cl for {0}", api);
        listApis.remove(listApis.get(i));
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
    context.setAttribute(API_READY, true);
  }

  /**
   * in case of failure.
   */
  @Override
  protected void ko() {
    // nothing to do.
  }

}
