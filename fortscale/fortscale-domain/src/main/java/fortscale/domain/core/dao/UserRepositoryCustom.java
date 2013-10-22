package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;

public interface UserRepositoryCustom {
	public User findByApplicationUserName(ApplicationUserDetails applicationUserDetails);
	public List<User> findByClassifierIdAndScoreBetween(String classifierId, int lowestVal, int upperVal, Pageable pageable);
}
