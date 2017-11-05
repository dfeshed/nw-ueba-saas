package presidio.output.domain.services.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;

import java.util.Collection;
import java.util.List;

/**
 * Created by efratn on 21/08/2017.
 */
public interface UserPersistencyService {
    User save(User user);

    Iterable<User> save(List<User> users);

    User findUserById(String id);

    Iterable<User> findAll();

    Page<User> findByUserName(String userName, PageRequest pageRequest);

    Page<User> findByUserId(String userId, PageRequest pageRequest);

    Page<User> find(UserQuery userQuery);

    Page<User> findByIds(Collection<String> ids, PageRequest pageRequest);
}
