package meccano.bridge.tomcat.commands;

import javax.servlet.ServletContext;
import meccano.bridge.tomcat.util.DecoratedLogger;

/**
 * Common interface for all command, implements setup.
 *
 * @author Thierry Uriot
 */
public abstract class Command {

  /** logger. */
  protected static final DecoratedLogger LOG = DecoratedLogger.getLogger(Command.class.getName());
  /** web context. */
  protected ServletContext context = null;
  /** repeat interval. */
  protected long interval = 10000L;
  /** repeat times. */
  protected int repeat = 10;

  /**
   * Initialize properties.
   * @param context from web listener
   * @param log logger
   * @param interval for retry
   * @param repeat for retry
   * @param contextName for tracing
   */
  public void setup(final ServletContext context, final long interval, final int repeat, final String contextName) {
    this.context = context;
    this.interval = interval;
    this.repeat = repeat;
    LOG.contextName = contextName;
  }

  /**
   *
   * @return succes
   */
  public abstract boolean run();

}
