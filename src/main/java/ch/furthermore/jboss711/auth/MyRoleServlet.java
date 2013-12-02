package ch.furthermore.jboss711.auth;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * curl -v -H "X-AuthenticationToken: webTestUser,myRole" http://localhost:8080/JB71AuthValveTest/myRole.html
 * 
 * => Will be successful (webTestUser)
 * 
 * curl -v -H "X-AuthenticationToken: webTestUser,wrongUser" http://localhost:8080/JB71AuthValveTest/myRole.html
 * 
 * => Will fail (403 Forbidden)
 */
@WebServlet(urlPatterns="/myRole.html")
public class MyRoleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	TestServiceRemote testService;
	
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		arg1.getWriter().println(testService.whoAmI());
	}
}
