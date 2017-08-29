package presidio.output.domain.services.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.repositories.UserRepository;

import java.util.List;

/**
 * Created by efratn on 21/08/2017.
 */
public class UserPersistencyServiceImpl implements UserPersistencyService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Iterable<User> save(List<User> users) {
        return userRepository.save(users);
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

    public Page<User> findByUserId(String userId, PageRequest pageRequest) {
        return userRepository.findByUserID(userId, pageRequest);
    }


    @Override
    public Page<User> find(UserQuery userQuery) {
        return userRepository.search(new UserElasticsearchQueryBuilder(userQuery).build());
    }

}
