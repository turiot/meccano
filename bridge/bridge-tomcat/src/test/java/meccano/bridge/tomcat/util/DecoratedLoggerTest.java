package meccano.bridge.tomcat.util;

import java.util.logging.Level;
import org.junit.Test;

public class DecoratedLoggerTest {
  
  @Test
  public void testLogger() {
    DecoratedLogger logger = DecoratedLogger.getLogger("test");
    logger.contextName = "context";
    logger.log(Level.INFO, "test {0}", "ok");
  }
  
}
