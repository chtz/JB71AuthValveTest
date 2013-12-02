package ch.furthermore.jboss711.auth;

import java.security.Principal;
import java.security.PrivilegedAction;

import javax.ejb.EJBAccessException;
import javax.naming.InitialContext;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jboss.security.SecurityContext;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SecurityContextFactory;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.jboss.security.identity.plugins.SimpleRoleGroup;

@WebListener
public class ProgrammaticLoginTest implements ServletContextListener {
    public void contextInitialized(ServletContextEvent arg0) {
    	new Thread(new Runnable() { //this thread is not associated with a principal yet
			@Override
			public void run() {
				String expectedUser = "random" + System.currentTimeMillis();
				
				if (expectedUser.equals(runTestServiceAs(expectedUser, "myRole,secondRole"))) {
					System.out.println("Run-as with correct role - successful, as expected!");
				}
				
				try {
					runTestServiceAs(expectedUser, "myOtherRole,secondRole");
					
					throw new IllegalStateException();
				} catch (EJBAccessException e) {
					System.out.println("Run-as with incorrect role - failed, as expected!");
				}
			}
		}).start();
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }
    
    private String runTestServiceAs(String user, String role) {
		return runAs("myDomain", user + "," + role, AuthenticationTokenLoginModule.NO_PASSWORD, new PrivilegedAction<String>() {
			@Override
			public String run() {
				try {
					InitialContext ctx = new InitialContext();
					
					TestServiceRemote example = (TestServiceRemote) ctx.lookup("java:global/JB71AuthValveTest/TestService");
					
					return example.whoAmI();
				} catch (RuntimeException e) {
					throw e;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
    
    private <T> T runAs(String securityDomain, String userName, String password, PrivilegedAction<T> action) {  
        SecurityContext originalSecurityContext = SecurityContextAssociation.getSecurityContext();  
        try {  
            LoginContext ctx = new LoginContext(securityDomain, new UsernamePasswordHandler(userName, password.toCharArray()));  
            ctx.login();  
  
            SecurityContext sc = SecurityContextFactory.createSecurityContext(securityDomain);
            
            for (Principal p : ctx.getSubject().getPrincipals()) {
            	if ("CallerPrincipal".equals(p.getName())) {
            		sc.getUtil().createSubjectInfo(((SimpleGroup) p).members().nextElement(), password, ctx.getSubject());
            	}
            	else if ("Roles".equals(p.getName())) {
            		sc.getUtil().setRoles(new SimpleRoleGroup((SimpleGroup) p));
            	} 
            }
            
            SecurityContextAssociation.setSecurityContext(sc);  
  
            return Subject.doAs(ctx.getSubject(), action);  
        } catch (RuntimeException e) {  
            throw e;  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        } finally {  
            SecurityContextAssociation.setSecurityContext(originalSecurityContext);  
        }  
    }  
}
