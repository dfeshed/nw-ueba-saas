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
public class UserServiceImpl implements UserService{

    private final EventPersistencyService eventPersistencyService;
    private final UserPersistencyService userPersistencyService;

    public UserServiceImpl(EventPersistencyService eventPersistencyService, UserPersistencyService userPersistencyService) {
        this.eventPersistencyService = eventPersistencyService;
        this.userPersistencyService = userPersistencyService;
    }

    @Override
    public User createUserEntity(String userId) {
        UserDetails userDetails = getUserDetails(userId);
        List<String> alertClassifications = new ArrayList<>(); //TODO
        List<String> indicators = new ArrayList<>(); //TODO
        double userScore = 0d; //TODO temporary hard coded value, to be changed when we will calculate user score
        User user = new User(userDetails.getUserId(),
                userDetails.getUserName(),
                userDetails.getUserDisplayName(),
                userScore,
                alertClassifications,
                indicators);

        return user;
    }

    @Override
    public void save(List<User> users) {
        userPersistencyService.save(users);
    }

    private UserDetails getUserDetails(String userId) {
        EnrichedEvent event = eventPersistencyService.findLatestEventForUser(userId);
        String userDisplayName = event.getUserDisplayName();
        String userName = event.getUserName();
        return new UserDetails(userName, userDisplayName, userId);
    }
}
