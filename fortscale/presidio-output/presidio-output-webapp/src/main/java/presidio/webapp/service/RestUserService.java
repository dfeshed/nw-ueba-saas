package presidio.webapp.service;


import presidio.webapp.model.Alert;
import presidio.webapp.model.User;
import presidio.webapp.model.UserQuery;

import java.util.List;

public interface RestUserService {
    User getUserById(String userId);

    List<User> getUsers(UserQuery userQurey);

    User createResult(presidio.output.domain.records.users.User user);

    List<Alert> getAlertsByUserId(String userId);

}
