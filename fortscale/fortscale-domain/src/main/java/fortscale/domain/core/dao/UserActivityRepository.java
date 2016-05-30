package fortscale.domain.core.dao;

import fortscale.domain.core.UserActivity;
import fortscale.domain.core.UserActivityLocation;

import java.util.List;


public interface UserActivityRepository {

    List<UserActivityLocation> getUserActivityLocationEntries(String username, int timeRangeInDays, int limit);

    List<UserActivity> findAll();
}
