package fortscale.domain.core.dao;

import fortscale.domain.core.activities.UserActivityLocationDocument;

import java.util.List;

public interface UserActivityLocationRepositoryCustom {

	List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays);
}
