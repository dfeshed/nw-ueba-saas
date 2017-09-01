package presidio.output.processor.services.user;

import presidio.output.domain.records.users.User;

import java.util.List;

/**
 * Created by efratn on 22/08/2017.
 */
public interface UserService {
    User createUserEntity(String userId);

    void save(List<User> users);

    void setClassification(User user, List<String> classification);

    public User findUserById(String userId);
}
