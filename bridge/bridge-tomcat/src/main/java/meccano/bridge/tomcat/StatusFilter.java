package meccano.bridge.tomcat;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static meccano.bridge.tomcat.Constants.*;

/**
 * Filter blocking access to the application if status not ready.
 *
 * @author Thierry Uriot
 */
public class StatusFilter implements Filter {

  /** application context */
  private ServletContext context;
  
  /**
   * If application status is 'ready' accept the request.
   * If application status is 'init' or 'failed' block the request.
   * statusservlet is not concerned.
   * @see Filter#doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
   */
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    if (((HttpServletRequest) req).getRequestURI().endsWith("/status") || STATUS_READY.equals(context.getAttribute(APP_STATUS))) {
      chain.doFilter(req, resp);
    }
    else {
      ((HttpServletResponse) resp).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
  }

  /** 
   * used to get context. 
   * @see Filter#init(FilterConfig) 
   */
  public void init(FilterConfig config) {
    context = config.getServletContext();
  }

  /**
   * to free context 
   * @see Filter#destroy()
   */
  public void destroy() {
    context = null;
  }

}
