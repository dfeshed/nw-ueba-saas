package fortscale.services.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.analyst.dao.AnalystAuthRepository;
import fortscale.domain.analyst.dao.AnalystRepository;
import fortscale.domain.core.EmailAddress;




@Service
public class MongoUserDetailsService implements UserDetailsService{
	
	@Autowired
	private AnalystAuthRepository analystAuthRepository;
	
	@Autowired
	private AnalystRepository analystRepository;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		AnalystAuth authenticatedUser = analystAuthRepository.findByUsername(username);
		if(authenticatedUser == null){
			throw new UsernameNotFoundException(username);
		}

		return authenticatedUser;
	}
	
	public void create(String username, String password,
			String emailAddress, String firstName, String lastName) {
		Assert.hasText(username);
		Assert.notNull(emailAddress);
		Assert.hasText(firstName);
		Assert.hasText(lastName);
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		AnalystAuth analystAuth = new AnalystAuth(username, encoder.encodePassword(password, null), authorities);
		analystAuthRepository.save(analystAuth);
		
		Analyst analyst = new Analyst(username, new EmailAddress(emailAddress), firstName, lastName);
		analystRepository.save(analyst);
	}

	/**
     * Update the specified user.
     */
    public void updateUser(UserDetails user) {
    	throw new UnsupportedOperationException();
    }

    /**
     * Remove the user with the given login name from the system.
     */
    public void deleteUser(String username) {
    	throw new UnsupportedOperationException();
    }

    /**
     * Modify the current user's password. This should change the user's password in
     * the persistent user repository (datbase, LDAP etc).
     *
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    public void changePassword(String oldPassword, String newPassword) {
    	throw new UnsupportedOperationException();
    }

    /**
     * Check if a user with the supplied login name exists in the system.
     */
    public boolean userExists(String username) {
    	return analystAuthRepository.findByUsername(username) != null ? true : false;
    }
}
