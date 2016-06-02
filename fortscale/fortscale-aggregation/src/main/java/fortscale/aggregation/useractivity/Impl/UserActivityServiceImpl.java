package fortscale.aggregation.useractivity.Impl;

import fortscale.aggregation.useractivity.services.UserActivityService;
import fortscale.domain.core.User;
import fortscale.domain.core.UserActivity;
import fortscale.domain.core.UserActivityLocation;
import fortscale.domain.core.dao.UserActivityLocationRepository;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("UserActivityService")
public class UserActivityServiceImpl implements UserActivityService {
    private final UserActivityLocationRepository userActivityLocationRepository;
    private final UserService userService;
    private static final Logger logger = Logger.getLogger(UserActivityServiceImpl.class);

    @Autowired
    public UserActivityServiceImpl(UserActivityLocationRepository userActivityLocationRepository, UserService userService) {
        this.userActivityLocationRepository = userActivityLocationRepository;
        this.userService = userService;
    }

    @Override
    public List<UserActivity> getUserActivities(String username) {
        return userActivityLocationRepository.findAll();
    }

    @Override
    public List<UserActivityLocation> getUserActivityLocationEntries(String id, int timeRangeInDays) {
        final User user = userService.getUserById(id);
        if (user == null) {
            final String errorMessage = String.format("Failed to get user-activity-location. User with id '%s' doesn't exist", id);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        return userActivityLocationRepository.getUserActivityLocationEntries(user.getUsername(), timeRangeInDays);
    }

}
