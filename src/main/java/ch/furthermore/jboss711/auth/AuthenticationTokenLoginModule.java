package ch.furthermore.jboss711.auth;

import java.security.Principal;
import java.security.acl.Group;
import java.util.StringTokenizer;

import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

/**
 * Required configuration in standalone.xml:
 * 
 * <pre>
 * <security-domains>
 *   ...
 *   <security-domain name="myDomain" cache-type="default">
 *     <authentication>
 *       <login-module code="ch.furthermore.jboss711.auth.AuthenticationTokenLoginModule" flag="required"/>
 *     </authentication>
 *     ...
 *   </security-domain>
 *   ...
 * </pre>
 */
public class AuthenticationTokenLoginModule extends UsernamePasswordLoginModule {
	public static final String NO_PASSWORD = "noPassword";

	@Override
	protected String getUsersPassword() throws LoginException {
		return NO_PASSWORD;
	}
	
	protected Principal getIdentity(){
		String identity = getTokens().nextToken();
		
		return new SimplePrincipal(identity);
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
		Group[] roleSets = { 
			new SimpleGroup("Roles")
		};
		
		StringTokenizer tokens = getTokens();
		tokens.nextToken();
		
		while (tokens.hasMoreTokens()) {
			String role = tokens.nextToken();
			
			roleSets[0].addMember(new SimplePrincipal(role));
		}
		
	    return roleSets;
	}
	
	private StringTokenizer getTokens() {
		return new StringTokenizer(super.getIdentity().getName(), ",");
	}
}
