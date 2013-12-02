package ch.furthermore.jboss711.auth;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Realm;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;

public class AuthenticationTokenHeaderAuthenticator extends AuthenticatorBase {
	@Override
	protected boolean authenticate(Request request, HttpServletResponse response, LoginConfig loginConfig) throws IOException {
		Principal principal = request.getUserPrincipal();

		if (principal != null) {
			return true;
		}

		Realm realm = request.getContext().getRealm();

		String token = request.getHeader("X-AuthenticationToken");
		
		principal = realm.authenticate(token, AuthenticationTokenLoginModule.NO_PASSWORD);

		if (principal != null) {
			request.setUserPrincipal(principal);
			
			return true;
		}
		
		response.sendError(403);

		return false;
	}
}
