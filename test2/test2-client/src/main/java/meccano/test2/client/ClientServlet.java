package meccano.test2.client;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import meccano.bridge.core.ServiceRegistry;
import meccano.test2.api.IService;

/**
 * For integration test purpose, http servlet calling a provided service.
 */
public class ClientServlet extends HttpServlet {

  private static final long serialVersionUID = 1721107356540817044L;

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    ServiceRegistry sr = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;
    String serviceName = "test2.service";
    Object s = sr.lookup(serviceName);
    IService service = (IService) s;
    String result = service.test();
    out.print(result);
    try {
      Class.forName("org.hsqldb.jdbcDriver");
      out.print(request.getParameter(" + classloader leak"));
    } catch (ClassNotFoundException ex) {
      out.print(" + no classloader leak");
    }
    out.flush();
  }
}
