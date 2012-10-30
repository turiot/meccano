package meccano.bridge.web;

/**
 * Status mbean implementation.
 *
 * @author Thierry Uriot
 */
public class BridgeStatus implements BridgeStatusMbean {

  /**
   * status.
   * 0 : not ready
   * 1 : ready
   */
  private int status;

  /**
   * Constructor.
   */
  public BridgeStatus() {
    status = 1;
  }

  /**
   * get status value.
   * @return value (0/1)
   */
  public int getStatus() {
    return status;
  }

  /**
   * set status value.
   * @param value status value
   */
  public void setStatus(final int value) {
    status = value;
  }

}
