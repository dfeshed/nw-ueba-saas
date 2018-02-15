package presidio.output.commons.services.user;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class UserPropertiesUpdateServiceImpl implements UserPropertiesUpdateService {
    private static final Logger log = Logger.getLogger(UserPropertiesUpdateServiceImpl.class);

    private final EventPersistencyService eventPersistencyService;
    private final OutputToCollectionNameTranslator outputToCollectionNameTranslator;
    private final String TAG_ADMIN = "admin";


    public UserPropertiesUpdateServiceImpl(EventPersistencyService eventPersistencyService, OutputToCollectionNameTranslator outputToCollectionNameTranslator) {
        this.eventPersistencyService = eventPersistencyService;
        this.outputToCollectionNameTranslator = outputToCollectionNameTranslator;
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
        List<String> collectionNames = collectionNamesByOrderForEvents();
        EnrichedEvent enrichedEvent = eventPersistencyService.findLatestEventForUser(user.getUserId(), collectionNames);
        if (!ObjectUtils.isEmpty(enrichedEvent)) {
            if (!Objects.equals(user.getUserDisplayName(), enrichedEvent.getUserDisplayName())) {
                user.setUserDisplayName(enrichedEvent.getUserDisplayName());
                isUpdated = true;
            }
            if (!Objects.equals(user.getUserId(), enrichedEvent.getUserId()) && !StringUtils.isEmpty(enrichedEvent.getUserId())) {
                user.setUserId(enrichedEvent.getUserId());
                isUpdated = true;
            }
            if (!Objects.equals(user.getUserName(), enrichedEvent.getUserName())) {
                user.setUserName(enrichedEvent.getUserName());
                user.setUserDisplayNameSortLowercase(enrichedEvent.getUserName());
                user.setIndexedUserName(enrichedEvent.getUserName());
                isUpdated = true;
            }
            List<String> userTags = user.getTags();

            boolean isAdmin = false;
            if (MapUtils.isNotEmpty(enrichedEvent.getAdditionalInfo()) && StringUtils.isNotEmpty(enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN))
                    && Boolean.parseBoolean(enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN))) {
                isAdmin = true;
            }

            // If the user marked as admin but the last event arrived without the admin tag -> remove the admin tag
            if (CollectionUtils.isNotEmpty(userTags) && userTags.contains(TAG_ADMIN) && !isAdmin) {
                userTags.remove(TAG_ADMIN);
                isUpdated = true;
            } else if (isAdmin) {
                if (userTags == null) {
                    userTags = new ArrayList<>();
                    user.setTags(userTags);
                }
                // If the user wasn't as admin but the last event arrived with the admin tag -> add the admin tag
                if (!userTags.contains(TAG_ADMIN)) {
                    userTags.add(TAG_ADMIN);
                    isUpdated = true;
                }
            }

        } else {
            log.debug("No events where found for this user , therefore cannot update user properties accordingly to latest event");
        }
        if (isUpdated) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public List<String> collectionNamesByOrderForEvents() {
        List<Schema> schemas = new ArrayList<>(Arrays.asList(Schema.AUTHENTICATION, Schema.FILE, Schema.ACTIVE_DIRECTORY));
        List<String> collections = new ArrayList<>();
        schemas.forEach(schema -> {
            collections.add(outputToCollectionNameTranslator.toCollectionName(schema));
        });
        return collections;
    }
}
