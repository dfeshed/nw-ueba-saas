package presidio.output.commons.services.user;

import fortscale.utils.logging.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.event.EventPersistencyService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserPropertiesUpdateServiceImpl implements UserPropertiesUpdateService {
    private static final Logger log = Logger.getLogger(UserPropertiesUpdateServiceImpl.class);

    private final EventPersistencyService eventPersistencyService;
    private final String TAG_ADMIN = "admin";


    public UserPropertiesUpdateServiceImpl(EventPersistencyService eventPersistencyService) {
        this.eventPersistencyService = eventPersistencyService;
    }

    /**
     * When trying to update user properties we search for the latest AuthenticationEnrichedEven if exist, if not we
     * search for FileEnrichedEvent if exist, if not we search for ActiveDirectoryEnrichedEvent.
     * If the user properties have change we return the updated user if not we return null.
     *
     * @param user - User object that maybe need to update properties
     * @return User with updated properties or null if there is no change.
     */
    @Override
    public User userPropertiesUpdate(User user) {
        boolean isUpdated = false;
        List<String> collectionNames = new ArrayList<>(Arrays.asList("output_authentication_enriched_events",
                "output_file_enriched_events", "output_active_directory_enriched_events"));
        EnrichedEvent enrichedEvent = eventPersistencyService.findLatestEventForUser(user.getUserId(), collectionNames);
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
                user.setUserDisplayNameSortLowercase(enrichedEvent.getUserName());
                user.setIndexedUserName(enrichedEvent.getUserName());
                isUpdated = true;
            }
            List<String> enrichedEventTags = null;
            List<String> userTags = user.getTags();
            if (!CollectionUtils.isEmpty(enrichedEvent.getAdditionalInfo()) && enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN) != null
                    && Boolean.parseBoolean(enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN))) {
                enrichedEventTags = new ArrayList<>();
                enrichedEventTags.add(TAG_ADMIN);
            }
            if ((CollectionUtils.isEmpty(enrichedEventTags) && !CollectionUtils.isEmpty(userTags))
                    || (!CollectionUtils.isEmpty(enrichedEventTags) && CollectionUtils.isEmpty(userTags))) {
                if (!CollectionUtils.isEmpty(enrichedEventTags)) {
                    user.setTags(enrichedEventTags);
                } else {
                    user.setTags(null);
                }
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
}
