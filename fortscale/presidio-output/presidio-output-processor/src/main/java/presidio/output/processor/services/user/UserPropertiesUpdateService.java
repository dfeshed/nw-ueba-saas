package presidio.output.processor.services.user;

import presidio.output.domain.records.users.User;

/**
 * Created by maors on 1/15/2018.
 */
public interface UserPropertiesUpdateService {

    void updateAllUsers();

    User userPropertiesUpdate(User user);
}
