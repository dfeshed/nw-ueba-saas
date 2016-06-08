package fortscale.services.impl;

import fortscale.domain.core.User;
import fortscale.domain.core.UserActivityLocationDocument;
import fortscale.domain.core.dao.UserActivityLocationRepository;
import fortscale.services.UserActivityService;
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
    public List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays) {
        //Todo: cache the id to user for all activities (they all use the same user)
        final User user = userService.getUserById(id);
        if (user == null) {
            final String errorMessage = String.format("Failed to get user-activity-location. User with id '%s' doesn't exist", id);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        return userActivityLocationRepository.getUserActivityLocationEntries(user.getUsername(), timeRangeInDays);
    }

}
