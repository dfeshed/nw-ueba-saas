package fortscale.domain.core.dao;

import fortscale.domain.core.activities.UserActivity;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("userActivityNetworkAuthenticationRepositoryImpl")
public class UserActivityNetworkAuthenticationRepositoryImpl implements UserActivityNetworkAuthenticationRepository {
	@Override
	public List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays) {
		return null;
	}

	@Override
	public List<UserActivity> findAll() {
		return null;
	}
}
