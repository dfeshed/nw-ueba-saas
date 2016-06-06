package fortscale.domain.core.dao;

import fortscale.domain.core.UserActivity;
import fortscale.domain.core.UserActivityLocation;

import java.util.List;


public interface UserActivityLocationRepository {

    List<UserActivityLocation> getUserActivityLocationEntries(String username, int timeRangeInDays);

    List<UserActivity> findAll();
}
