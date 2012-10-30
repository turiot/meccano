package meccano.bridge.web;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listener adding core bridge libraries to tomcat classloader.
 * and signaling completion to other wars via mbean server
 *
 * @author Thierry Uriot
 */
public class BridgeContextListener implements ServletContextListener {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(BridgeContextListener.class.getName());

  /**
   * application initialization.
   *
   * @param event event
   */
  @SuppressWarnings("rawtypes")
  public void contextInitialized(final ServletContextEvent event) {
    final ServletContext context = event.getServletContext();
    try {
      LOG.log(Level.INFO, "------- starting bridge");
      final String path = context.getRealPath("/") + "WEB-INF/lib";
      LOG.log(Level.INFO, "------- path : {0}", path);
      final File libsDir = new File(path);
      String libName = "";
      boolean foundlib = false;
      final String[] libs = libsDir.list();
      if (libs != null) {
        for (int i = 0; i < libs.length; i++) {
          libName = libs[i];
          if (libName.startsWith("bridge-core")) {
            LOG.log(Level.INFO, "------- lib : {0}", libName);
            foundlib = true;
            break;
          }
        }
      }
      if (foundlib) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final URLClassLoader parent = (URLClassLoader) cl.getParent();
        final Class<?> sysclass = URLClassLoader.class;
        final Class<?>[] parameters = new Class[]{URL.class};
        final Method method = sysclass.getDeclaredMethod("addURL", parameters);
        method.setAccessible(true);
        method.invoke(parent, new File(path + '/' + libName).toURI().toURL());
      }
      final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      final BridgeStatus bs = new BridgeStatus();
      if (!foundlib) {
        bs.setStatus(0);
      }
      final StandardMBean mb = new StandardMBean(bs, BridgeStatusMbean.class);
      final String mbname = context.getInitParameter("meccano.mbname");
      mbs.registerMBean(mb, new ObjectName(mbname));

      LOG.log(Level.INFO, "------- started bridge");
    } catch (MalformedObjectNameException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (NotCompliantMBeanException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (MBeanRegistrationException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (InstanceAlreadyExistsException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (IllegalAccessException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (NoSuchMethodException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (InvocationTargetException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (MalformedURLException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    }
  }

  /**
   * application end.
   *
   * @param event event
   */
  public void contextDestroyed(final ServletContextEvent event) {
    LOG.log(Level.INFO, "------- ending bridge");
    try {
      final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      if (mbs != null) {
        final String mbname = event.getServletContext().getInitParameter("meccano.mbname");
        mbs.unregisterMBean(new ObjectName(mbname));
        LOG.log(Level.INFO, "------- end bridge");
      }
    } catch (InstanceNotFoundException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (MBeanRegistrationException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    } catch (MalformedObjectNameException e) {
      LOG.log(Level.SEVERE, "mbean error", e);
    }
  }

}
