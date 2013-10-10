package fortscale.domain.core.dao;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;

public interface UserRepositoryCustom {
	public User findByApplicationUserName(ApplicationUserDetails applicationUserDetails);
}
