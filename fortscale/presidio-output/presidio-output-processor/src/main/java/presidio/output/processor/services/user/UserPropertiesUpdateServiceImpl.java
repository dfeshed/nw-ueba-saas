package presidio.output.processor.services.user;

import fortscale.utils.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.ArrayList;
import java.util.List;


public class UserPropertiesUpdateServiceImpl implements UserPropertiesUpdateService {
    private static final Logger log = Logger.getLogger(UserPropertiesUpdateServiceImpl.class);

    private final EventPersistencyService eventPersistencyService;
    private final UserPersistencyService userPersistencyService;
    private final String TAG_ADMIN = "admin";

    private int defaultUsersBatchSize;

    public UserPropertiesUpdateServiceImpl(EventPersistencyService eventPersistencyService, UserPersistencyService userPersistencyService, int defaultUsersBatchSize) {
        this.eventPersistencyService = eventPersistencyService;
        this.userPersistencyService = userPersistencyService;
        this.defaultUsersBatchSize = defaultUsersBatchSize;
    }

    @Override
    public User userPropertiesUpdate(User user) {
        boolean isUpdated = false;
        EnrichedEvent enrichedEvent = eventPersistencyService.findLatestEventForUser(user.getUserId());
        if (!ObjectUtils.isEmpty(enrichedEvent)) {
            if (!user.getUserDisplayName().equals(enrichedEvent.getUserDisplayName())) {
                user.setUserDisplayName(enrichedEvent.getUserDisplayName());
                isUpdated = true;
            }
            if (!user.getUserId().equals(enrichedEvent.getUserId())) {
                user.setUserId(enrichedEvent.getUserId());
                isUpdated = true;
            }
            if (!user.getUserName().equals(enrichedEvent.getUserName())) {
                user.setUserName(enrichedEvent.getUserName());
                user.setIndexedUserName(enrichedEvent.getUserName());
                isUpdated = true;
            }
            List<String> tags;
            if (enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN) != null
                    && Boolean.parseBoolean(enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN))
                    && !user.getTags().isEmpty() && !user.getTags().contains(TAG_ADMIN)) {
                tags = user.getTags();
                tags.add(TAG_ADMIN);
                user.setTags(tags);
                isUpdated = true;
            }
        } else {
            log.debug("No events where found for this user");
        }
        if (isUpdated) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public void updateAllUsers() {
        UserQuery.UserQueryBuilder userQueryBuilder =
                new UserQuery.UserQueryBuilder()
                        .pageNumber(0)
                        .pageSize(defaultUsersBatchSize);
        Page<User> page = userPersistencyService.find(userQueryBuilder.build());
        while (page != null && page.hasContent()) {
            log.debug("Going over {} users", page.getSize());
            List<User> users = page.getContent();
            List<User> updatedUsers = new ArrayList<>();
            users.forEach(user -> {
                User returnedUser = userPropertiesUpdate(user);
                if (returnedUser != null) {
                    updatedUsers.add(returnedUser);
                }
            });
            if (updatedUsers.size() > 0) {
                log.debug("updating {} users", updatedUsers.size());
                userPersistencyService.save(updatedUsers);
            }
            if (page.hasNext()) {
                Pageable pageable = page.nextPageable();
                userQueryBuilder.pageNumber(pageable.getPageNumber());
                page = userPersistencyService.find(userQueryBuilder.build());
            } else {
                page = null;
            }
        }
    }
}
