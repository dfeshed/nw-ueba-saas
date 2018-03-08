package presidio.data.generators.user;

import presidio.data.domain.User;
import presidio.data.generators.common.GeneratorException;

public class SingleAdminUserGenerator implements IUserGenerator {
    private User user;

    public SingleAdminUserGenerator(String username) throws GeneratorException {
        user = new User(username);
        if (username.length() < 2){
            throw new GeneratorException("Generator Exception occurred: username should be at least 2 characters");
        }

        user.setUserId(username);
        user.setLastName(username.substring(username.length() - 2));
        user.setFirstName(username.substring(0, username.length() - 2));
        user.setAdministrator(true);
        user.setAnonymous(false);
    }

    public User getNext(){
        return this.user;
    }
}
