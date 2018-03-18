package fortscale.services.security;

import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;


public class MongoAuthenticationProvider extends DaoAuthenticationProvider {
	protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		String p = userDetails.getPassword();
		try {
			super.additionalAuthenticationChecks(userDetails, authentication);
		} catch (BadCredentialsException e) {
			if (authentication.getCredentials() == null) {
	            logger.debug("Authentication failed: no credentials provided");
	            
	            throw new BadCredentialsException(e.getMessage(), e);
	        }

	        String presentedPassword = authentication.getCredentials().toString();
	        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
	        if (!encoder.isPasswordValid(p, presentedPassword, null)) {
	            logger.debug("Authentication failed: password does not match stored value");

	            throw new BadCredentialsException(e.getMessage(), e);
	        }
			
	        try {
				((MongoUserDetailsService)getUserDetailsService()).changePassword(userDetails.getUsername(), presentedPassword);
			} catch (InvalidCredentialsException e1) {
				throw new BadCredentialsException(e1.getMessage(), e1);
			}
		}
	}
}
