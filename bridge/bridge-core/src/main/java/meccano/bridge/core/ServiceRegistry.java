package meccano.bridge.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry of services.
 *
 * @author Thierry Uriot
 */
public class ServiceRegistry {

  /**
   * map of services instances.
   */
  private final Map<String, Object> map;

  /**
   * constructor.
   */
  private ServiceRegistry() {
    map = new HashMap<String, Object>(20, 1.0f);
  }

  /**
   * add a service into the registry.
   *
   * @param name : service name
   * @param service : service instance
   */
  public void bind(final String name, final Object service) {
    map.put(name, service);
  }

  /**
   * get a service from the registry.
   *
   * @param name : service name
   * @return service instance
   */
  public Object lookup(final String name) {
    return map.get(name);
  }

  /**
   * remove a service from the registry.
   *
   * @param name : service name
   */
  public void unbind(final String name) {
    map.remove(name);
  }

  /**
   * Singleton container.
   */
  public enum Singleton {
    INSTANCE;
    public ServiceRegistry serviceRegistry;
    private Singleton() {
      serviceRegistry = new ServiceRegistry();
    }
  }
  
}
