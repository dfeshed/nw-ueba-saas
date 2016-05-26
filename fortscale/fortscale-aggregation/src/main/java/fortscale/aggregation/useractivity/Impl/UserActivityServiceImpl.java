package fortscale.aggregation.useractivity.Impl;

import fortscale.aggregation.useractivity.services.UserActivityService;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.UserActivity;
import fortscale.domain.core.dao.LocationEntry;
import fortscale.domain.core.dao.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("UserActivityService")
public class UserActivityServiceImpl implements UserActivityService {

    private static final String KEY_LOCATION_ENTRY = "locationEntries";
    private final UserActivityRepository userActivityRepository;

    @Autowired
    public UserActivityServiceImpl(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Override
    public List<UserActivity> getUserActivities() {
        return userActivityRepository.findAll();
    }

    @Override
    public List<LocationEntry> getLocationEntries(int timeRangeInDays, int limit) {
       return userActivityRepository.getLocationEntries(timeRangeInDays, limit);
    }
    }
}
