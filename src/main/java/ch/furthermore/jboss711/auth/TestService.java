package ch.furthermore.jboss711.auth;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
@Remote(TestServiceRemote.class)
public class TestService implements TestServiceRemote {
	@Resource
	SessionContext ctx;

	@RolesAllowed("myRole") 
	@Override
	public String whoAmI() {
		return ctx.getCallerPrincipal().getName();
	}
}
