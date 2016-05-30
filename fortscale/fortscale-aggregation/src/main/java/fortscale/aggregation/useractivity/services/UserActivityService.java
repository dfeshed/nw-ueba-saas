package fortscale.aggregation.useractivity.services;

import fortscale.domain.core.UserActivity;
import fortscale.domain.core.UserActivityLocation;

import java.util.List;

public interface UserActivityService {
    List<UserActivity> getUserActivities(String username);

    List<UserActivityLocation> getUserActivityLocationEntries(String id, int timeRangeInDays, int limit);


}
