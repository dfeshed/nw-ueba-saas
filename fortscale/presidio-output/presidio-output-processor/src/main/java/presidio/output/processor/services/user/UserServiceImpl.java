package presidio.output.processor.services.user;

import presidio.output.domain.records.users.User;
import presidio.output.domain.services.event.EventPersistencyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by efratn on 22/08/2017.
 */
public class UserServiceImpl implements UserService{

    private EventPersistencyService eventPersistencyService;

    public UserServiceImpl(EventPersistencyService eventPersistencyService) {
        this.eventPersistencyService = eventPersistencyService;
    }

    @Override
    public User createUserEntity(UserDetails userDetails) {
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
}
