package fortscale.services.analyst.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.analyst.AnalystFollowUser;
import fortscale.domain.analyst.dao.AnalystFollowUserRepository;
import fortscale.domain.analyst.dao.AnalystRepository;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.analyst.AnalystService;
import fortscale.services.exceptions.UnknownResourceException;



@Service("analystService")
public class AnalystServiceImpl implements AnalystService{
	
	@Autowired
	private AnalystRepository analystRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AnalystFollowUserRepository analystFollowUserRepository;
	
	

	@Override
	public void replaceEmailAddress(String username, String emailAddress) {
		Assert.hasText(username);
		Assert.notNull(emailAddress);
		
		
		Analyst analyst = analystRepository.findByUserName(username);
		if(analyst == null) {
			throw new UsernameNotFoundException(username);
		}
		
		if(!analyst.getEmailAddress().equals(new EmailAddress(emailAddress))) {
			analystRepository.save(analyst);
		}
	}



	@Override
	public Analyst findByUsername(String username) {
		return analystRepository.findByUserName(username);
	}



	@Override
	public List<Analyst> findAll() {
		return analystRepository.findAll();
	}



	@Override
	public List<Analyst> findAllNonDisabledUsers() {
		return analystRepository.findByIsDisabled(false);
	}



	@Override
	public void followUser(AnalystAuth analystAuth, String userId, boolean follow) {
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("There is no user with such id: %s", userId));
		}
		if(user.getFollowed() != follow){
			AnalystFollowUser analystFollowUser = new AnalystFollowUser(analystAuth.getId(), analystAuth.getUsername(), userId, user.getUsername(), follow);
			analystFollowUserRepository.save(analystFollowUser);
			userRepository.updateFollowed(user, follow);
		}
	}




}
