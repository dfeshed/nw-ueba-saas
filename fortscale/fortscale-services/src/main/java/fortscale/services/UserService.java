package fortscale.services;

import java.util.List;

import fortscale.domain.core.User;

public interface UserService {

	public User getUserById(String uid);
	
	public void updateUserWithCurrentADInfo();
	
	public void updateUserWithADInfo(String timestamp);
	
	public List<User> findBySearchFieldContaining(String prefix);
	
	public List<IUserScore> getUserScores(String uid); 
	
}
