package meccano.bridge.tomcat.commands;

import java.util.logging.Level;

/**
 * Abstract interface of a command waiting for bridge core, containing singletons apiregistry and serviceregistry.
 *
 * @author Thierry Uriot
 */
public abstract class WaitCommand extends Command {

  /**
   * loop template.
   * call following methods to be implemented.
   * @return success
   */
  @Override
  public boolean run() {
    boolean success = false;
    try {
      LOG.log(Level.INFO, "INIT");
      final boolean initok = init();
      if (initok) {
        boolean goOn = true;
        int cpt = 0;
        while (goOn) {
          LOG.log(Level.INFO, "TEST");
          final boolean ready = test();
          if (ready) {
            goOn = false;
            LOG.log(Level.INFO, "OK");
            ok();
            success = true;
          }
          else {
            if (cpt < repeat) {
              LOG.log(Level.INFO, "SLEEP");
              Thread.sleep(interval);
              cpt++;
            }
            else {
              goOn = false;
              LOG.log(Level.INFO, "KO");
              ko();
            }
          }
        }
      }
    } catch (InterruptedException ex) {
      LOG.log(Level.INFO, "------- ERREUR {0}", ex);
    }
    return success;
  }

  /**
   * init method.
   * @return success
   */
  protected abstract boolean init();

  /**
   * repeated test method.
   * @return success
   */
  protected abstract boolean test();

  /**
   * successful end.
   */
  protected abstract void ok();

  /**
   * unsuccessful end.
   */
  protected abstract void ko();

}
