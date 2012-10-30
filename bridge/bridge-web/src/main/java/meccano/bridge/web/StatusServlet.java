package meccano.bridge.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet giving status.
 *
 * @author Thierry Uriot
 */
public class StatusServlet extends HttpServlet {
  
  private static final long serialVersionUID = -658231640401538720L;

  /**
	 * Return status of the application.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    out.print("ready");
    out.flush();
	}

}
