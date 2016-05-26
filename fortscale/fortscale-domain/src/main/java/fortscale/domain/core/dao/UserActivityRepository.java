package fortscale.domain.core.dao;

import fortscale.domain.core.UserActivity;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserActivityRepository {

    @Query("SELECT location FROM mycollection where t.id = :id")
    List<LocationEntry> getLocationEntries(int timeRangeInDays, int limit);

    List<UserActivity> findAll();
}
