package presidio.output.processor.services.user;

import presidio.output.domain.records.users.User;

/**
 * Created by efratn on 22/08/2017.
 */
public interface UserService {
    User createUserEntity(UserDetails userDetails);
}
