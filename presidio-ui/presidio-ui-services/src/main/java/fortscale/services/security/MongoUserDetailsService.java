package fortscale.services.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
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
import fortscale.utils.logging.Logger;




@Service
public class MongoUserDetailsService implements UserDetailsService, InitializingBean{
	private static Logger logger = Logger.getLogger(MongoUserDetailsService.class);
	
	@Autowired
	private AnalystAuthRepository analystAuthRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SaltSource saltSource;
	
	@Autowired
	private AnalystRepository analystRepository;
//	@Value("${analyst.first.admin.username}" )
//	private String firstAdminUserName;
//	@Value("${analyst.first.admin.password}" )
//	private String firstAdminPassword;
//	@Value("${analyst.first.admin.firstname}" )
//	private String firstAdminFirstName;
//	@Value("${analyst.first.admin.lastname}" )
//	private String firstAdminLastName;
	

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
			String emailAddress, String firstName, String lastName) throws Exception {
		Assert.hasText(username);
		Assert.hasText(emailAddress);
		Assert.hasText(firstName);
		Assert.hasText(lastName);
		
		if(analystAuthRepository.findByUsername(username) != null || analystRepository.findByUserName(username) != null) {
			throw new Exception(String.format("analyst with username %s already exist", username));
		}
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(AnalystAuth.ROLE_ADMIN));
		AnalystAuth analystAuth = new AnalystAuth(username, "", authorities);
		analystAuth.setCredentialsNonExpired(false);
		analystAuth.setPassword(encodePassword(analystAuth, password));
		analystAuthRepository.save(analystAuth);
		
		Analyst analyst = new Analyst(username, new EmailAddress(emailAddress), firstName, lastName);
		analystRepository.save(analyst);
	}

	/**
     * Update the specified user.
     */
    public void updateUser(String oldUsername, String newUsername, String newPassword, String newEmailAddress, String newFirstName, String newLastName) {
    	AnalystAuth analystAuth = analystAuthRepository.findByUsername(oldUsername);
    	if(analystAuth == null){
    		throw new UsernameNotFoundException(oldUsername);
    	}
    	
    	if(newUsername != null || newPassword != null) {
	    	if(newUsername != null) {
	    		analystAuth.setUsername(newUsername);
	    	}
	    	if(newPassword != null) {
	    		analystAuth.setPassword(encodePassword(analystAuth, newPassword));
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
    
    public String encodePassword(UserDetails userDetails, String password) {
    	return passwordEncoder.encodePassword(password, saltSource.getSalt(userDetails));
	}

    /**
     * disable the user with the given login name.
     */
    public void disableUser(String username) {
    	Analyst analyst = analystRepository.findByUserName(username);
    	if(analyst == null){
    		throw new UsernameNotFoundException(username);
    	}
		analyst.setDisabled(true);
		analystRepository.save(analyst);
		
    	AnalystAuth analystAuth = analystAuthRepository.findByUsername(username);
    	analystAuth.setAccountNonExpired(false);
    	analystAuthRepository.save(analystAuth);
    }
    
    /**
     * disable the user with the given login name.
     */
    public void enableUser(String username) {
    	Analyst analyst = analystRepository.findByUserName(username);
    	if(analyst == null){
    		throw new UsernameNotFoundException(username);
    	}
    	if(analyst.isDisabled()){
			analyst.setDisabled(false);
			analystRepository.save(analyst);
		
	    	AnalystAuth analystAuth = analystAuthRepository.findByUsername(username);
	    	analystAuth.setAccountNonExpired(true);
	    	analystAuthRepository.save(analystAuth);
    	}
    }

    /**
     * Modify the current user's password. This should change the user's password in
     * the persistent user repository (datbase, LDAP etc).
     *
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     * @throws InvalidCredentialsException 
     */
    public void changePassword(String username, String oldPassword, String newPassword) throws InvalidCredentialsException {
    	AnalystAuth analystAuth = getAnalystAuthByUsernameAndPassword(username, oldPassword);
    	
    	analystAuth.setPassword(encodePassword(analystAuth, newPassword));
		analystAuth.setCredentialsNonExpired(true);
    	analystAuthRepository.save(analystAuth);
    }
    
    public void changePassword(String username, String newPassword) throws InvalidCredentialsException {
    	changePassword(username, newPassword, true);
    }
    
    public void changePassword(String username, String newPassword, boolean isCredentialsNonExpired) throws InvalidCredentialsException {
    	AnalystAuth analystAuth = analystAuthRepository.findByUsername(username);
    	if(analystAuth == null){
    		throw new UsernameNotFoundException(username);
    	}
    	
    	analystAuth.setPassword(encodePassword(analystAuth, newPassword));
		analystAuth.setCredentialsNonExpired(isCredentialsNonExpired);
    	analystAuthRepository.save(analystAuth);
    }

    /**
     * Check if a user with the supplied login name exists in the system.
     */
    public boolean userExists(String username) {
    	return analystAuthRepository.findByUsername(username) != null ? true : false;
    }
    
    public AnalystAuth getAnalystAuthByUsernameAndPassword(String username, String password) throws InvalidCredentialsException{
    	AnalystAuth analystAuth = analystAuthRepository.findByUsername(username);
    	if(analystAuth == null){
    		throw new UsernameNotFoundException(username);
    	}
    	
    	if(!encodePassword(analystAuth,password.toString()).equals(analystAuth.getPassword())){
    		throw new InvalidCredentialsException("wrong password");
    	}

    	return analystAuth;
    }
    
    public void validatePassword(String username, String password) throws InvalidCredentialsException{
    	getAnalystAuthByUsernameAndPassword(username, password);
    }

	@Override
	public void afterPropertiesSet() throws Exception {
//		if(analystAuthRepository.count() == 0) {
//			try {
//				create(firstAdminUserName, firstAdminPassword, firstAdminUserName, firstAdminFirstName, firstAdminLastName);
//			} catch (Exception e) {
//				logger.error("got the following error while trying to add the first analyst record.", e);
//			}
//		}
	}
}
