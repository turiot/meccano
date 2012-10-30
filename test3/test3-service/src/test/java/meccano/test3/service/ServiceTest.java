package meccano.test3.service;

import java.util.logging.Logger;
import meccano.test3.api.IService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Service testing.
 */
public class ServiceTest {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(ServiceTest.class.getName());

  /**
   * Main test.
   */
  @Test
  public void testService() {
    LOG.info("service test");
    ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
    IService service = (IService) ctx.getBean("service");
    service.test();
  }
}
