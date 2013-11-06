package fortscale.service.domain.core.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.Threshold;



@Component("userRepository")
public class UserRepositoryImpl implements UserRepository{
	
	private Map<String, User> userMap = new HashMap<String, User>();


	@Override
	public Page<User> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<User> findAll(Iterable<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(User entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends User> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User findByEmailAddress(EmailAddress emailAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findByLastnameContaining(String lastNamePrefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findByAdUserPrincipalName(String adUserPrincipalName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findByAdDn(String adDn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findBySearchFieldContaining(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findByAdUserPrincipalNameContaining(
			String adUserPrincipalNamePrefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findByApplicationUserName(
			ApplicationUserDetails applicationUserDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findByClassifierIdAndScoreBetween(String classifierId,
			int lowestVal, int upperVal, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countNumOfUsersAboveThreshold(String classifierId,
			Threshold threshold) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public User findByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findAll(Sort arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends User> List<S> save(Iterable<S> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
