package meccano.bridge.web;

/**
 * Status mbean interface.
 *
 * @author Thierry Uriot
 */
public interface BridgeStatusMbean {

  /**
   * get status value.
   * @return value (0/1)
   */
  int getStatus();

  /**
   * set status value.
   * @param value status value
   */
  void setStatus(int value);

}
