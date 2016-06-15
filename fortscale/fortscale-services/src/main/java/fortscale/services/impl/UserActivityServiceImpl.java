package fortscale.services.impl;

import fortscale.domain.core.User;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityDataUsageDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.domain.core.dao.UserActivityRepository;
import fortscale.services.UserActivityService;
import fortscale.services.UserService;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("UserActivityService")
public class UserActivityServiceImpl implements UserActivityService {

	private static final Logger logger = Logger.getLogger(UserActivityServiceImpl.class);

	@Autowired
	private CacheHandler<String, String> idToUsernameCache;

    private final UserActivityRepository userActivityRepository;
    private final UserService userService;


    @Autowired
    public UserActivityServiceImpl(UserActivityRepository userActivityRepository, UserService userService) {
        this.userActivityRepository = userActivityRepository;
        this.userService = userService;
    }

    @Override
    public List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays) {
        final String username = getUsernameById(id);
        return userActivityRepository.getUserActivityLocationEntries(username, timeRangeInDays);
    }

    @Override
    public List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String id,
			int timeRangeInDays) {
        final String username = getUsernameById(id);
        return userActivityRepository.getUserActivityNetworkAuthenticationEntries(username, timeRangeInDays);
    }

    @Override
    public List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays) {
        return userActivityRepository.getOrganizationActivityLocationEntries(timeRangeInDays);
    }

    @Override
    public List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String id, int timeRangeInDays){
        final String username = getUsernameById(id);
        return userActivityRepository.getUserActivitySourceMachineEntries(username, timeRangeInDays);
    }

    @Override
	public List<UserActivityDataUsageDocument> getUserActivityDataUsageEntries(String id, int timeRangeInDays) {
		final String username = getUsernameById(id);
		return userActivityRepository.getUserActivityDataUsageEntries(username, timeRangeInDays);
    }

    private String getUsernameById(String id) {
        String username = idToUsernameCache.get(id);
		if (username == null) {
			User user = userService.getUserById(id);
			if (user != null) {
				username = user.getUsername();
				idToUsernameCache.put(id, username);
			}
		}
		if (username == null) {
			String error = String.format("Failed to get user activity. User with id '%s' doesn't exist", id);
			logger.error(error);
			throw new RuntimeException(error);
		}
		return username;
    }

}