package meccano.bridge.tomcat.commands;

import java.util.ArrayList;
import java.util.logging.Level;
import meccano.bridge.core.ApiRegistry;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Asynchronous api registring.
 *
 * @author Thierry Uriot
 */
public class SetApiCommand extends Command {

  /**
   * For parametrized api, add a new classloader into the global api registry.
   * @return succes
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean run() {
    boolean success = false;
    final ApiRegistry ar = ApiRegistry.Singleton.INSTANCE.apiRegistry;
    final String ctxpath = context.getRealPath("/") + "WEB-INF/lib/";

    final ArrayList<String> givenApis = (ArrayList<String>) context.getAttribute(GIVEN_APIS);
    for (final String api : givenApis) {
      final String path = ctxpath + api + ".jar";
      LOG.log(Level.INFO, "------- give api {0}", path);
      success = ar.addClassLoader(api, path);
      if (!success) {
        break;
      }
    }

    LOG.log(Level.INFO, "end set api");
    context.setAttribute(SETAPI_READY, true);
    return success;
  }

}
