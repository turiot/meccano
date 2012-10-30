<!DOCTYPE html>
<%@page import="meccano.test1.api.IService"%>
<%@page import="meccano.bridge.core.ServiceRegistry"%>
<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>test1 client</h2>
      <%
          ServiceRegistry sr = ServiceRegistry.getInstance();
          Object s = sr.lookup("service-1.0");    
          IService service = (IService) s;
          out.println(service.sayHelloFromBean());
      %>
    </body>
</html>
