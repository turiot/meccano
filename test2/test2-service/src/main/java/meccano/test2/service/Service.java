package meccano.test2.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import meccano.test2.api.IService;

/**
 * Service implementation.
 */
public class Service implements IService {

  /**
   * logger.
   */
  private static final Logger LOG = Logger.getLogger(Service.class.getName());

  /**
   * method implementation.
   *
   * @return String
   */
  public String test() {
    LOG.info("service called");
    String ret = "";
    
    Connection c = null;
    Statement s = null;
    try {
      Class.forName("org.hsqldb.jdbcDriver");
      String url = "jdbc:hsqldb:mem:tempdb";
      String user = "sa";
      String password = "";
      c = DriverManager.getConnection(url, user, password);
      s = c.createStatement();
      s.execute("create temporary table t1 (c1 int)");
      s.execute("insert into t1 values (123)");
      ret = "jdbc ok";
      s.close();
    } catch (SQLException ex) {
      Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
      ret = "sql execption";
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
      ret = "driver not found";
    }
    finally {
      try {
        if (s!=null) {
          s.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
      }
      try {
        if (c!=null) {
          c.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    return ret;
  }
}
