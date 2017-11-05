package presidio.output.domain.services.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.repositories.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by efratn on 21/08/2017.
 */
public class UserPersistencyServiceImpl implements UserPersistencyService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(User user) {
        user.updateFieldsBeforeSave();
        return userRepository.save(user);
    }

    @Override
    public Iterable<User> save(List<User> users) {
        if (users != null && users.size() > 0) {
            users.forEach(user -> user.updateFieldsBeforeSave());
            return userRepository.save(users);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public User findUserById(String id) {
        return userRepository.findOne(id);
    }

    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findByUserName(String userName, PageRequest pageRequest) {
        return userRepository.findByUserName(userName, pageRequest);
    }

    public Page<User> findByIds(Collection<String> ids, PageRequest pageRequest) {
        return userRepository.findByIdIn(ids, pageRequest);
    }

    public Page<User> findByUserId(String userId, PageRequest pageRequest) {
        return userRepository.findByUserId(userId, pageRequest);
    }

    @Override
    public Page<User> find(UserQuery userQuery) {
        return userRepository.search(new UserElasticsearchQueryBuilder(userQuery).build());
    }
}
