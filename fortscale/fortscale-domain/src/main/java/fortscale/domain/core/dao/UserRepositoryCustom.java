package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.fe.dao.Threshold;

public interface UserRepositoryCustom {
	public User findByApplicationUserName(ApplicationUserDetails applicationUserDetails);
	public List<User> findByApplicationUserName(String applicationName, List<String> usernames);
	public User findByApplicationUserName(String applicationName, String username);
	public List<User> findByClassifierIdAndScoreBetween(String classifierId, int lowestVal, int upperVal, Pageable pageable);
	public List<User> findByClassifierIdAndFollowedAndScoreBetween(String classifierId, int lowestVal, int upperVal, Pageable pageable);
	public int countNumOfUsersAboveThreshold(String classifierId, Threshold threshold);
	public int countNumOfUsers(String classifierId);
	public User findByLogUsername(String logname, String username);
	public void updateFollowed(User user, boolean followed);
}
