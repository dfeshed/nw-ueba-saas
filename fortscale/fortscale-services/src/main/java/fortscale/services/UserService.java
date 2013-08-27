package fortscale.services;

import java.util.List;

import fortscale.domain.core.User;

public interface UserService {

	public User getUserById(String uid);
	
	public void updateUserWithCurrentADInfo();
	
	public List<User> findBySearchFieldContaining(String prefix);
	
	
}
