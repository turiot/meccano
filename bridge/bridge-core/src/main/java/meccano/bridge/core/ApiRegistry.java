package meccano.bridge.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registry of common api's.
 *
 * @author Thierry Uriot
 */
public class ApiRegistry {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(ApiRegistry.class.getName());
  /**
   * map of class loaders.
   */
  private final Map<String, ClassLoader> loaders;

  /**
   * Constructor.
   */
  private ApiRegistry() {
    loaders = new HashMap<String, ClassLoader>(20, 1.0f); // no diamond to be 1.5 compatible
  }

  /**
   * Add a classloader in the registry.
   *
   * @param api api name
   * @param path jar path
   * @return success boolean value
   */
  public boolean addClassLoader(final String api, final String path) {
    boolean success = false;
    final File jar = new File(path);
    LOG.log(Level.INFO, "*** add {0}", api);
    try {
      final URL url = jar.toURI().toURL();
      final URL[] urls = {url};
      final ClassLoader cl = new URLClassLoader(urls); // beware security
      loaders.put(api, cl);
      success = true;
    } catch (MalformedURLException e) {
      LOG.log(Level.SEVERE, "url error", e);
    }
    return success;
  }

  /**
   * get classloader for an api name.
   *
   * @param api name
   * @return classloader
   */
  public ClassLoader getClassLoaderForName(final String api) {
    return loaders.get(api);
  }

  /**
   * Singleton container.
   */
  public enum Singleton {
    INSTANCE;
    public ApiRegistry apiRegistry;
    private Singleton() {
      apiRegistry = new ApiRegistry();
    }
  }
  
}
