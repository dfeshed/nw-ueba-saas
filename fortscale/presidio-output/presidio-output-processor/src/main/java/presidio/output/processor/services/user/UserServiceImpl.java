package presidio.output.processor.services.user;

import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by efratn on 22/08/2017.
 */
public class UserServiceImpl implements UserService {

    private final EventPersistencyService eventPersistencyService;
    private final UserPersistencyService userPersistencyService;

    public UserServiceImpl(EventPersistencyService eventPersistencyService, UserPersistencyService userPersistencyService) {
        this.eventPersistencyService = eventPersistencyService;
        this.userPersistencyService = userPersistencyService;
    }

    @Override
    public User createUserEntity(String userId) {
        UserDetails userDetails = getUserDetails(userId);
        return new User(userDetails.getUserId(), userDetails.getUserName(), userDetails.getUserDisplayName(), userDetails.isAdmin());
    }

    @Override
    public User findUserById(String userId) {
        return userPersistencyService.findUserById(userId);
    }

    @Override
    public void save(List<User> users) {
        userPersistencyService.save(users);
    }

    private UserDetails getUserDetails(String userId) {
        EnrichedEvent event = eventPersistencyService.findLatestEventForUser(userId);
        String userDisplayName = event.getUserDisplayName();
        String userName = event.getUserName();
        Boolean isAdmin = Boolean.valueOf(event.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN));
        return new UserDetails(userName, userDisplayName, userId, isAdmin);
    }

    public void setClassification(User user, List<String> classification) {
        user.addAlertClassifications(classification);
    }

    @Override
    public void setUserAlertData(User user, List<String> classification, List<String> indicators) {
        user.setAlertClassifications(classification);
        user.setIndicators(indicators);
    }
}
