package meccano.bridge.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Simple test.
 */
public class ServiceiRegistryTest {

  /**
   * test method.
   */
  @Test
  public void testGetClassLoaderForName() {
    final ServiceRegistry registry = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;
    final Integer object = Integer.valueOf(123);
    registry.bind("test", object);
    final Integer obj = (Integer) registry.lookup("test");
    assertEquals("error", object, obj);
  }

}
