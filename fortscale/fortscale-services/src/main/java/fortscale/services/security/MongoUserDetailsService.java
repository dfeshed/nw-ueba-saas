package fortscale.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.dao.AnalystRepository;




@Service
public class MongoUserDetailsService implements UserDetailsService{
	
	@Autowired
	private AnalystRepository analystRepository;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		Analyst analyst = analystRepository.findByUserName(username);
		if(analyst == null){
			throw new UsernameNotFoundException(username);
		}
		User authenticatedUser = new User(analyst.getUserName(),
				analyst.getPassword().toLowerCase(), analyst.isEnabled(),
				analyst.isAccountNonExpired(), analyst.isCredentialsNonExpired(),
				analyst.isAccountNonLocked(), analyst.getAuthorities());
		return authenticatedUser;
	}

}
