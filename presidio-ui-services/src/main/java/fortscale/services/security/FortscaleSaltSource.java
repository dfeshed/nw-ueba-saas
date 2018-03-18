package fortscale.services.security;

import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;

public class FortscaleSaltSource implements SaltSource {

	@Override
	public Object getSalt(UserDetails user) {
		return String.format("%s%s", "52482dcce4b0b780ca4986a6", user.getUsername());
	}

}
