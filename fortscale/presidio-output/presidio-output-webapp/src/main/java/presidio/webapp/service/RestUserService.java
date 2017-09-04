package presidio.webapp.service;


import org.springframework.data.domain.Page;
import presidio.output.domain.records.users.UserQuery;
import presidio.webapp.model.Alert;
import presidio.webapp.model.User;

import java.util.List;

public interface RestUserService {
    User getUserById(String userId);

    List<User> getUsers(UserQuery userQurey);

    User createResult(presidio.output.domain.records.users.User user);

    Page<Alert> getAlertsByUserId(String userId);

}
