package ch.furthermore.jboss711.auth;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * curl -v -H "X-AuthenticationToken: webTestUser,otherRole" http://localhost:8080/JB71AuthValveTest/otherRole.html
 * 
 * => Will fail (500 Internal Server Error)
 * 
 * curl -v -H "X-AuthenticationToken: webTestUser,wrongRole" http://localhost:8080/JB71AuthValveTest/otherRole.html
 * 
 * => Will fail (403 Forbidden)
 */
@WebServlet(urlPatterns="/otherRole.html")
public class OtherRoleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	TestServiceRemote testService;
	
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		arg1.getWriter().println(testService.whoAmI());
	}
}
