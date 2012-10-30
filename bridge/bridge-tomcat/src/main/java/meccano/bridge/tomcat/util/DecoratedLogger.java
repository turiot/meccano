package meccano.bridge.tomcat.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Decorated log methods.
 *
 * @author Thierry Uriot
 */
public class DecoratedLogger extends Logger {

  /** context name. */
  public String contextName = "";
  
  /** constructor. */
  protected DecoratedLogger(String name) {
    super(name, null);
    setUseParentHandlers(false);
    ConsoleHandler ch = new ConsoleHandler();
    ch.setFormatter(new CustomFormatter());
    addHandler(ch);
  }
  
  /** factory. */
  public static DecoratedLogger getLogger(String name) {
		LogManager manager = LogManager.getLogManager();
		DecoratedLogger logger = (DecoratedLogger) manager.getLogger(name);
		if (logger == null) {
      logger = new DecoratedLogger(name);
      manager.addLogger(logger);
    }
		return logger;
  }
  
  /** specific (compact) formatter. */
  public class CustomFormatter extends Formatter {
    /** date - level [context] (class.methode) message. */
    public String format(LogRecord record) {
      return
        dateFormat(new Date()) + " - " +
        record.getLevel().getLocalizedName() + ' ' +
        '['+contextName+"] (" + 
        record.getSourceClassName() + '.' + record.getSourceMethodName()+ ") " +
        formatMessage(record) + '\n';
    }
    /** shared parser. */
    private final SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss.SSS");
    /** synchro overhead not relevant here. */
    private synchronized String dateFormat(Date date) {
      return format.format(date);
    }
    
  }  
  
}
