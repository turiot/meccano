package meccano.bridge.tomcat.util;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Servlet giving status.
 *
 * @author Thierry Uriot
 */
public class StatusServlet extends HttpServlet {
  
  private static final long serialVersionUID = -658911640401538720L;

  /**
	 * Return status of the application.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    out.print(getServletContext().getAttribute(APP_STATUS));
    out.flush();
	}

}
