package presidio.output.commons.services.user;

import presidio.output.domain.records.users.User;

import java.util.List;


public interface UserPropertiesUpdateService {

    User userPropertiesUpdate(User user);

    List<String> collectionNamesByOrderForEvents();
}
