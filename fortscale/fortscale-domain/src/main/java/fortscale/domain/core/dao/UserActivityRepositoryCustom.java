package fortscale.domain.core.dao;

import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;

import java.util.List;

public interface UserActivityRepositoryCustom {

	List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays);
	List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String username, int timeRangeInDays);

}
