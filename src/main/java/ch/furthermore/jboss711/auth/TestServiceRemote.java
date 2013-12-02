package ch.furthermore.jboss711.auth;

import javax.ejb.Remote;

@Remote
public interface TestServiceRemote {
	public String whoAmI();
}
