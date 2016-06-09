package fortscale.domain.core.dao;

import fortscale.domain.core.activities.UserActivity;
import fortscale.domain.core.activities.UserActivityLocationDocument;

import java.util.List;

public interface UserActivityNetworkAuthenticationRepository {

	List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays);

	List<UserActivity> findAll();
}
