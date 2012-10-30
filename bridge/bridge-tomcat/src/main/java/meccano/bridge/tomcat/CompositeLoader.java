package meccano.bridge.tomcat;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classloader (Tomcat specific) looking first in shared api's.
 *
 * @author Thierry Uriot
 */
public class CompositeLoader extends ClassLoader {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(CompositeLoader.class.getName());
  /**
   * shared api's.
   */
  private final List<ClassLoader> classLoaders;
  /**
   * parent classloader (WebappClassloader).
   */
  private final ClassLoader parentClassLoader;

  /**
   * Constructor.
   *
   * @param classLoader parent
   */
  public CompositeLoader(final ClassLoader classLoader) {
    classLoaders = new ArrayList<ClassLoader>(20);
    parentClassLoader = classLoader;
  }

  /**
   * add a shared api classloader to the list.
   *
   * @param loader a classloader
   */
  public void addClassLoader(final ClassLoader loader) {
    classLoaders.add(loader);
    LOG.info("--- addcl");
  }

  /**
   * lookup first in the list of classloaders then in normal ones.
   * @param name resource name
   * @return url of resource
   */
  @Override
  public URL getResource(final String name) {
    for (final ClassLoader classLoader : classLoaders) {
      final URL customResource = classLoader.getResource(name);
      if (customResource != null) {
        return customResource;
      }
    }
    return parentClassLoader.getResource(name);
  }

  /**
   * lookup first in the list of classloaders then in normal ones.
   * @param name class name
   * @return found class
   * @throws ClassNotFoundException usual exception
   */
  @Override
  public Class<?> loadClass(final String name) throws ClassNotFoundException {
    LOG.log(Level.INFO, "--- LOAD {0}", name);
    Class<?> clazz = null;
    boolean foundcustom = false;
    for (final ClassLoader classLoader : classLoaders) {
      try {
        clazz = classLoader.loadClass(name);
        foundcustom = true;
        break;
      }
      catch (ClassNotFoundException e) {
        foundcustom = false;
      }
    }
    if (foundcustom) {
      LOG.log(Level.INFO, "--- found 1 {0}", name);
    }
    else {
      LOG.log(Level.INFO, "--- not found 1 {0}", name);
      try {
        clazz = parentClassLoader.loadClass(name);
        LOG.log(Level.INFO, "--- found 2 {0}", name);
      }
      catch (ClassNotFoundException e) {
        LOG.log(Level.SEVERE, "--- not found 2 {0}", name);
        throw e;
      }
    }
    return clazz;
  }

  /**
   * Custom toString for debug.
   * @return textual information
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(200);
    sb.append("composite (").append(getClass().getName()).append(") ").append(classLoaders.size());
    return sb.toString();
  }
}
