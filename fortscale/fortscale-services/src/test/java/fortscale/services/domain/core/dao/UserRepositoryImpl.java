package fortscale.services.domain.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.Threshold;



@Component("userRepository")
public class UserRepositoryImpl implements UserRepository{
	
	private Map<String, User> userMap = new HashMap<String, User>();
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Override
	public Page<User> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends User> S save(S entity) {
		userMap.put(entity.getId(), entity);
		return entity;
	}

	@Override
	public User findOne(String id) {
		return userMap.get(id);
	}
	

	@Override
	public boolean exists(String id) {
		
		return false;
	}

	@Override
	public Iterable<User> findAll(Iterable<String> ids) {
		
		return null;
	}

	@Override
	public long count() {
		
		return 0;
	}

	@Override
	public void delete(String id) {
		
		
	}

	@Override
	public void delete(User entity) {
		
		
	}

	@Override
	public void delete(Iterable<? extends User> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public User findByEmailAddress(EmailAddress emailAddress) {
		
		return null;
	}

	@Override
	public List<User> findByLastnameContaining(String lastNamePrefix) {
		
		return null;
	}

	@Override
	public User findByAdUserPrincipalName(String adUserPrincipalName) {
		
		return null;
	}

	@Override
	public User findByAdDn(String adDn) {
		
		return null;
	}

	@Override
	public List<User> findBySearchFieldContaining(String prefix) {
		
		return null;
	}

	@Override
	public List<User> findByAdUserPrincipalNameContaining(
			String adUserPrincipalNamePrefix) {
		
		return null;
	}

	@Override
	public User findByApplicationUserName(
			ApplicationUserDetails applicationUserDetails) {
		
		return null;
	}

	@Override
	public List<User> findByClassifierIdAndScoreBetween(String classifierId,
			int lowestVal, int upperVal, Pageable pageable) {
		return new ArrayList<>(userMap.values());
	}

	@Override
	public int countNumOfUsersAboveThreshold(String classifierId,
			Threshold threshold) {
		
		if(threshold.getValue() == 100) {
			return 0;
		}
		int val = 100 - threshold.getValue();
		val = val * 10 + val / 10 + 1;
		if(threshold.getValue() == 0) {
			val = val * 4;
		}
		
		return val;
	}

	@Override
	public User findByUsername(String username) {
		
		return null;
	}

	@Override
	public List<User> findAll() {
		
		return null;
	}

	@Override
	public List<User> findAll(Sort arg0) {
		
		return null;
	}

	@Override
	public <S extends User> List<S> save(Iterable<S> arg0) {
		
		return null;
	}

	@Override
	public List<User> findByApplicationUserName(String applicationName,
			List<String> usernames) {
		
		return null;
	}

	@Override
	public List<User> findByUsernameContaining(String username) {
		
		return null;
	}

	@Override
	public int countNumOfUsers(String classifierId) {
		
		return 0;
	}

	@Override
	public User findByApplicationUserName(String applicationName,
			String username) {
		
		return null;
	}

	@Override
	public List<User> findByUsernameRegex(String usernameRegex) {
		
		return null;
	}

	@Override
	public User findByLogUsername(String logname, String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
