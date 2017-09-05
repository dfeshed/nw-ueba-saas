package presidio.webapp.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.model.Alert;
import presidio.webapp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maors on 9/3/2017.
 */
public class RestUserServiceImpl implements RestUserService {

    private final RestAlertService restAlertService;
    private final UserPersistencyService userPersistencyService;
    private final int PAGE = 100;
    private final int SIZE = 100;

    public RestUserServiceImpl(RestAlertService restAlertService, UserPersistencyService userPersistencyService) {
        this.restAlertService = restAlertService;
        this.userPersistencyService = userPersistencyService;
    }

    @Override
    public User getUserById(String userId) {
        return createResult(userPersistencyService.findByUserId(userId, new PageRequest(PAGE, SIZE)).iterator().next());
    }

    @Override
    public List<User> getUsers(UserQuery userQurey) {
        userPersistencyService.find(userQurey);
        Page<presidio.output.domain.records.users.User> users = userPersistencyService.find(userQurey);
        List<User> restUsers = new ArrayList<>();
        for (presidio.output.domain.records.users.User user : users) {
            restUsers.add(createResult(user));
        }
        return restUsers;
    }

    @Override
    public User createResult(presidio.output.domain.records.users.User user) {
        return null;
    }

    @Override
    public List<Alert> getAlertsByUserId(String userId) {
        return restAlertService.getAlertsByUserId(userId);
    }
}
