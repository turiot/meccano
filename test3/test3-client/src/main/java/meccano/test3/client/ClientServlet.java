package meccano.test3.client;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import meccano.bridge.core.ServiceRegistry;
import meccano.test3.api.IService;
import org.springframework.web.HttpRequestHandler;

/**
 * For integration test purpose, http servlet calling a provided service.
 */
public class ClientServlet implements HttpRequestHandler {

  /**
   * Call service and print return String.
   */
  public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    ServiceRegistry sr = ServiceRegistry.Singleton.INSTANCE.serviceRegistry;
    String serviceName = "test3.service";
    IService service = (IService) sr.lookup(serviceName);
    String result = "service called by " + service.test();
    out.print(result);
    out.flush();
  }
  
}
 