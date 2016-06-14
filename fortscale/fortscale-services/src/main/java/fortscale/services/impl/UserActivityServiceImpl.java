package fortscale.services.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fortscale.domain.core.User;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.dao.UserActivityRepository;
import fortscale.services.UserActivityService;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("UserActivityService")
public class UserActivityServiceImpl implements UserActivityService {
    private final UserActivityRepository userActivityRepository;
    private final UserService userService;
    private final LoadingCache<String, User> usersCache = createUsersCache();

    private static final Logger logger = Logger.getLogger(UserActivityServiceImpl.class);

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
    public List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String id, int timeRangeInDays) {
        final String username = getUsernameById(id);
        return userActivityRepository.getUserActivityNetworkAuthenticationEntries(username, timeRangeInDays);
    }

    @Override
    public List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays) {
        return userActivityRepository.getOrganizationActivityLocationEntries(timeRangeInDays);
    }


    private String getUsernameById(String id) {
        try {
            return usersCache.getUnchecked(id).getUsername();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to get user activity. User with id '%s' doesn't exist", id));
        }
    }

    private LoadingCache<String, User> createUsersCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(10000)
                .concurrencyLevel(6) //this is supposed to be the number of activities. EDIT this if you have more activities
                .expireAfterAccess(1, TimeUnit.DAYS)
                .build(
                        new CacheLoader<String, User>() {
                            @ParametersAreNonnullByDefault
                            public @NotNull User load(String key) throws Exception {
                                return userService.getUserById(key);
                            }
                        });
    }

}
