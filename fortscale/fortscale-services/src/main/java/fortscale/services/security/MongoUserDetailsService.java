package fortscale.services.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
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
			String emailAddress, String firstName, String lastName) throws AlreadyExistsException {
		Assert.hasText(username);
		Assert.notNull(emailAddress);
		Assert.hasText(firstName);
		Assert.hasText(lastName);
		
		if(analystAuthRepository.findByUsername(username) != null || analystRepository.findByUserName(username) != null) {
			throw new AlreadyExistsException(String.format("analyst with username %s already exist", username));
		}
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		AnalystAuth analystAuth = new AnalystAuth(username, encodePassword(password), authorities);
		analystAuth.setCredentialsNonExpired(false);
		analystAuthRepository.save(analystAuth);
		
		Analyst analyst = new Analyst(username, new EmailAddress(emailAddress), firstName, lastName);
		analystRepository.save(analyst);
	}

	/**
     * Update the specified user.
     */
    public void updateUser(String oldUsername, String newUsername, String newPassword, String newEmailAddress, String newFirstName, String newLastName) {
    	AnalystAuth analystAuth = analystAuthRepository.findByUsername(oldUsername);
    	if(newUsername != null || newPassword != null) {
	    	if(newUsername != null) {
	    		analystAuth.setUsername(newUsername);
	    	}
	    	if(newPassword != null) {
	    		analystAuth.setPassword(encodePassword(newPassword));
	    	}
	    	analystAuthRepository.save(analystAuth);
    	}
    	if(newUsername != null || newEmailAddress != null || newFirstName != null || newLastName != null) {
    		Analyst analyst = analystRepository.findByUserName(oldUsername);
    		if(newUsername != null) {
    			analyst.setUserName(newUsername);
	    	}
	    	if(newEmailAddress != null) {
	    		analyst.setEmailAddress(new EmailAddress(newEmailAddress));
	    	}
	    	if(newFirstName != null) {
	    		analyst.setFirstName(newFirstName);
	    	}
	    	if(newLastName != null) {
	    		analyst.setLastName(newLastName);
	    	}
	    	analystRepository.save(analyst);
    	}
    }
    
    public String encodePassword(String password) {
    	String retString = null;
		if(password != null) {
			Md5PasswordEncoder encoder = new Md5PasswordEncoder();
			retString = encoder.encodePassword(password, null);
		}
		return retString;
	}

    /**
     * disable the user with the given login name.
     */
    public void disableUser(String username) {
    	AnalystAuth analystAuth = analystAuthRepository.findByUsername(username);
    	analystAuth.setAccountNonExpired(false);
    }

    /**
     * Modify the current user's password. This should change the user's password in
     * the persistent user repository (datbase, LDAP etc).
     *
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
    	AnalystAuth analystAuth = analystAuthRepository.findByUsername(username);
    	String encodedPasswordString = encodePassword(newPassword);
    	if(analystAuth.getPassword().equals(encodePassword(oldPassword)) && !oldPassword.equals(newPassword)) {
    		analystAuth.setPassword(encodedPasswordString);
    		analystAuth.setCredentialsNonExpired(true);
	    	analystAuthRepository.save(analystAuth);
    	}
    }

    /**
     * Check if a user with the supplied login name exists in the system.
     */
    public boolean userExists(String username) {
    	return analystAuthRepository.findByUsername(username) != null ? true : false;
    }
}
