package meccano.bridge.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import static meccano.bridge.tomcat.Constants.*;
import meccano.bridge.tomcat.commands.Command;
import meccano.bridge.tomcat.commands.RunListenersCommand;
import meccano.bridge.tomcat.commands.SetApiCommand;
import meccano.bridge.tomcat.commands.SetServiceCommand;
import meccano.bridge.tomcat.commands.WaitBridgeCommand;
import meccano.bridge.tomcat.commands.WaitServiceCommand;
import meccano.bridge.tomcat.util.DecoratedLogger;

/**
 * Thread launching commands sequentially.
 *
 * @author Thierry Uriot
 */
public class CommandController implements Runnable {

  /** logger. */
  private static final DecoratedLogger LOG = DecoratedLogger.getLogger(CommandController.class.getName());
  /** web context. */
  private final ServletContext context;

  /**
   * Constructor.
   * @param context the web context
   * @param log logger
   */
  public CommandController(final ServletContext context) {
    this.context = context;
    LOG.contextName = context.getServletContextName();
  }

  /**
   * Main method.
   * get from meccano.properties the elements to wait for/set
   * accordingly execute necessary commands
   * lazy reflective instanciation needed because of differed class loading
   */
  @SuppressWarnings({"rawtypes","unchecked"})
  public void run() {
    try {
      final InputStream fin = CommandController.class.getClassLoader().getResourceAsStream("./meccano.properties");
      if (fin == null) {
        LOG.log(Level.SEVERE, "meccano.properties not found");
      }
      else {
        final Properties props = new Properties();
        try { // no try-with-resource to be 1.5 compatible
          props.load(fin);
        }
        catch (IOException e) {
          LOG.log(Level.SEVERE, "------- ERREUR {0}", e);
        }
        finally {
          try {
            fin.close();
          }
          catch (IOException e) {
            // nothing on close
          }
        }

        final ArrayList<String> neededApis = new ArrayList<String>(2);
        final ArrayList<String> neededServices = new ArrayList<String>(1);
        final ArrayList<String> givenApis = new ArrayList<String>(2);
        final ArrayList<String> givenServices = new ArrayList<String>(1);
        final ArrayList<String> givenClasses = new ArrayList<String>(1);
        for (final Object obj : props.keySet()) {
          final String key = (String) obj;
          if (key.startsWith("need.api")) {
            neededApis.add(props.getProperty(key));
          }
          if (key.startsWith("need.service")) {
            neededServices.add(props.getProperty(key));
          }
          if (key.startsWith("give.api")) {
            givenApis.add(props.getProperty(key));
          }
          if (key.startsWith("give.service")) {
            givenServices.add(props.getProperty(key));
          }
          if (key.startsWith("give.class")) {
            givenClasses.add(props.getProperty(key));
          }
        }
        for (final String res : neededApis)     { LOG.log(Level.INFO, "neededApi     {0}", res); }
        for (final String res : neededServices) { LOG.log(Level.INFO, "neededService {0}", res); }
        for (final String res : givenApis)      { LOG.log(Level.INFO, "givenApi      {0}", res); }
        for (final String res : givenServices)  { LOG.log(Level.INFO, "givenService  {0}", res); }
        for (final String res : givenClasses)   { LOG.log(Level.INFO, "givenClass    {0}", res); }

        int repeat = 0;
        final String repeatParam = context.getInitParameter("meccano.repeat");
        if (repeatParam != null) {
          repeat = Integer.parseInt(repeatParam);
        }
        long interval = 0L;
        final String intervalParam = context.getInitParameter("meccano.interval");
        if (intervalParam != null) {
          interval = Long.parseLong(intervalParam);
        }
        LOG.log(Level.INFO, "repeat        {0}", repeat);
        LOG.log(Level.INFO, "interval      {0}", interval);

        final ArrayList<String> customListeners = new ArrayList<String>(2);
        final Enumeration<String> keys = context.getInitParameterNames();
        final List<String> sorted = Collections.list(keys);
        Collections.sort(sorted);
        for (final String key : sorted) {
          if (key.startsWith("custom.listener")) {
            customListeners.add(context.getInitParameter(key));
          }
        }
        for (final String string : customListeners) { LOG.log(Level.INFO, "customListener {0}", string); }

        final ArrayList<Command> commands = new ArrayList<Command>(5);
        commands.add(new WaitBridgeCommand());
        context.setAttribute(BRIDGE_READY, false);
        if (!neededApis.isEmpty()) {
          commands.add(new meccano.bridge.tomcat.commands.WaitApiCommand());
          context.setAttribute(NEEDED_APIS, neededApis);
          context.setAttribute(API_READY, false);
        }
        if (!neededServices.isEmpty()) {
          commands.add(new WaitServiceCommand());
          context.setAttribute(NEEDED_SERVICES, neededServices);
          context.setAttribute(SERVICE_READY, false);
        }
        if (!givenApis.isEmpty()) {
          commands.add(new SetApiCommand());
          context.setAttribute(GIVEN_APIS, givenApis);
          context.setAttribute(SETAPI_READY, false);
        }
        if (!givenServices.isEmpty()) {
          commands.add(new SetServiceCommand());
          context.setAttribute(GIVEN_SERVICES, givenServices);
          context.setAttribute(GIVEN_CLASSES, givenClasses);
          context.setAttribute(SETSERVICE_READY, false);
        }
        if (!customListeners.isEmpty()) {
          commands.add(new RunListenersCommand());
          context.setAttribute(CUSTOM_LISTENERS, customListeners);
        }

        boolean success = true;
        for (final Command command : commands) {
          command.setup(context, interval, repeat, context.getServletContextName());
          final boolean commandsuccess = command.run();
          if (!commandsuccess) {
            success = false;
            break;
          }
        }
        if (success) {
          context.setAttribute(APP_STATUS, STATUS_READY);
        }
        else {
          LOG.log(Level.SEVERE, "------- APPLI NOT READY");
          context.setAttribute(APP_STATUS, STATUS_FAILED);
        }
      }
    }
    catch (NumberFormatException e) {
      LOG.log(Level.SEVERE, "------- ERREUR {0}", e);
    }
  }

}
