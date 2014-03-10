package fortscale.services.analyst.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.analyst.AnalystFollowUser;
import fortscale.domain.analyst.AnalystSavedSearch;
import fortscale.domain.analyst.dao.AnalystFollowUserRepository;
import fortscale.domain.analyst.dao.AnalystRepository;
import fortscale.domain.analyst.dao.AnalystSavedSearchRepository;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.analyst.AnalystService;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.utils.logging.Logger;



@Service("analystService")
public class AnalystServiceImpl implements AnalystService{
	private static Logger logger = Logger.getLogger(AnalystServiceImpl.class);
	
	@Autowired
	private AnalystRepository analystRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AnalystFollowUserRepository analystFollowUserRepository;
	
	@Autowired
	private AnalystSavedSearchRepository analystSavedSearchRepository;
	

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
	public String getAnalystDisplayName(String username) {
		Analyst analyst = analystRepository.findByUserName(username);
		return (analyst!=null)? analyst.getFirstName() + " " + analyst.getLastName() : null;
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



	@Override
	public String createSavedSearch(AnalystAuth analystAuth, String name,	String category, String filter, String description) {
		if(analystSavedSearchRepository.findByNameAndCategory(name, category) != null){
			logger.info("a save search with name ({}) and category ({}) already exist", name, category);
			throw new IllegalArgumentException(String.format("a save search with name (%s) and category (%s) already exist", name, category));
		}
		
		DBObject filterDbObject = (DBObject) JSON.parse(filter);
		AnalystSavedSearch analystSavedSearch = new AnalystSavedSearch(analystAuth.getId(), analystAuth.getUsername(), name, category, filterDbObject);
		analystSavedSearch.setDescription(description);
		analystSavedSearch = analystSavedSearchRepository.save(analystSavedSearch);
		
		return analystSavedSearch.getId();
	}

	@Override
	public List<AnalystSavedSearch> findSavedSearch(String savedSearchId){
		List<AnalystSavedSearch> ret = null;
		if(savedSearchId != null){
			AnalystSavedSearch analystSavedSearch = analystSavedSearchRepository.findOne(savedSearchId);
			if(analystSavedSearch != null){
				ret = new ArrayList<>();
				ret.add(analystSavedSearch);
			} else{
				ret = Collections.emptyList();
			}
		} else{
			ret = analystSavedSearchRepository.findAll();
		}
		
		return ret;
	}


}
