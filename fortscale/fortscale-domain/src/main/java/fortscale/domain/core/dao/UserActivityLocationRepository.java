package fortscale.domain.core.dao;

import fortscale.domain.core.UserActivity;
import fortscale.domain.core.UserActivityLocationDocument;

import java.util.List;


public interface UserActivityLocationRepository {

    List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays);

    List<UserActivity> findAll();
}
