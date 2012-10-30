package meccano.test1.client;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import meccano.bridge.core.ServiceRegistry;
import meccano.test1.api.IService;
import meccano.test1.api.Model1;

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
    String serviceName = "service-1.0";
    Object s = sr.lookup(serviceName);
    if (s == null) {
      out.print("service '"+serviceName+"' not found in registry");
      //request.getSession().getServletContext().setAttribute(APP_STATUS, STATUS_FAILED);
    }
    else {
      IService service = (IService) s;
      Model1 model = service.getValue();
      out.print(model.getAttribut());
    }
    out.flush();
  }
}
