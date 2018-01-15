package presidio.output.processor.services.user;

import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.event.EventPersistencyService;

import java.util.List;


public class UserPropertiesUpdateServiceImpl implements UserPropertiesUpdateService {

    private EventPersistencyService eventPersistencyService;
    private final String TAG_ADMIN = "admin";

    public UserPropertiesUpdateServiceImpl(EventPersistencyService eventPersistencyService) {
        this.eventPersistencyService = eventPersistencyService;
    }

    @Override
    public User userPropertiesUpdate(User user) {
        EnrichedEvent enrichedEvent = eventPersistencyService.findLatestEventForUser(user.getUserId());
        if (!ObjectUtils.isEmpty(enrichedEvent)) {
            if (!user.getUserDisplayName().equals(enrichedEvent.getUserDisplayName())) {
                user.setUserDisplayName(enrichedEvent.getUserDisplayName());
            }
            if (!user.getUserId().equals(enrichedEvent.getUserId())) {
                user.setUserId(enrichedEvent.getUserId());
            }
            if (!user.getUserName().equals(enrichedEvent.getUserName())) {
                user.setUserName(enrichedEvent.getUserName());
                user.setIndexedUserName(enrichedEvent.getUserName());
            }
            List<String> tags;
            if (enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN) != null
                    && Boolean.parseBoolean(enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN))
                    && !user.getTags().isEmpty() && !user.getTags().contains(TAG_ADMIN)) {
                tags = user.getTags();
                tags.add(TAG_ADMIN);
                user.setTags(tags);
            }
        }
        return user;
    }

}
