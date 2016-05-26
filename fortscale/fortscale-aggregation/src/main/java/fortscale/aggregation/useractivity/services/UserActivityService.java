package fortscale.aggregation.useractivity.services;

import fortscale.domain.core.UserActivity;
import fortscale.domain.core.dao.LocationEntry;

import java.util.List;

public interface UserActivityService {
    List<UserActivity> getUserActivities();

    List<LocationEntry> getLocationEntries(int timeRangeInDays, int limit);
}
