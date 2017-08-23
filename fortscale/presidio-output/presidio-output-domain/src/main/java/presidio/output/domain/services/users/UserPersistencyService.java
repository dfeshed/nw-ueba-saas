package presidio.output.domain.services.users;

import org.springframework.data.domain.Page;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;

import java.util.List;

/**
 * Created by efratn on 21/08/2017.
 */
public interface UserPersistencyService {
    User save(User user);

    Iterable<User> save(List<User> users);

    User findUserById(String id);

    Iterable<User> findAll();

    Page<User> find(UserQuery userQuery);
}
