package presidio.webapp.service;


import presidio.webapp.model.Alert;
import presidio.webapp.model.User;
import presidio.webapp.model.UserQuery;

import java.util.List;

public interface RestUserService {
    User getUserById(String userId, boolean expand);

    List<User> getUsers(UserQuery userQuery);

    User createResult(presidio.output.domain.records.users.User user, List<Alert> alerts);

    List<Alert> getAlertsByUserId(String userId);

}
