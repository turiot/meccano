package meccano.test1.api;

/**
 * service contract.
 */
public interface IService {

  /**
   * simple method.
   *
   * @return String
   */
  String sayHelloFromBean();

  /**
   * a method with return value.
   *
   * @return Object
   */
  Model1 getValue();
}
