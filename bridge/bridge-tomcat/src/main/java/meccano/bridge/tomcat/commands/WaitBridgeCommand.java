package meccano.bridge.tomcat.commands;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Asynchronous waiting for bridge core, containing singletons apiregistry and serviceregistry.
 *
 * @author Thierry Uriot
 */
public class WaitBridgeCommand extends WaitCommand {

  /** ref to tomcat mbean server. */
  private MBeanServer mbs;
  /** mbean name. */
  private String mbname;

  /**
   * get tomcat mbean server.
   * @return succees
   */
  @Override
  protected boolean init() {
    boolean success = true;
    mbs = ManagementFactory.getPlatformMBeanServer();
    mbname = context.getInitParameter("meccano.mbname");
    if (mbname == null) {
      success = false;
    }
    return success;
  }

  /**
   * testing presence, can be called many times.
   * @return success
   */
  @Override
  protected boolean test() {
    boolean success = false;
    try {
      final ObjectName mbeanname = new ObjectName(mbname);
      success = mbs.isRegistered(mbeanname);
      if (success) {
        success = false;
        final int status = ((Integer) mbs.getAttribute(mbeanname, "Status")).intValue();
        if (status == 1) {
          success = true;
        }
      }
    } catch (MalformedObjectNameException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (MBeanException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (AttributeNotFoundException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (InstanceNotFoundException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (ReflectionException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    }
    return success;
  }

  /**
   * in case of success.
   */
  @Override
  protected void ok() {
    context.setAttribute(BRIDGE_READY, true);
  }

  /**
   * in case of failure.
   */
  @Override
  protected void ko() {
    // nothing to do
  }

}
