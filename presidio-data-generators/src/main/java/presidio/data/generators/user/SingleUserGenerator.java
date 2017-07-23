package presidio.data.generators.user;

import presidio.data.generators.common.GeneratorException;
import presidio.data.domain.User;

public class SingleUserGenerator implements IUserGenerator {
    private User user;

    public SingleUserGenerator(String username) throws GeneratorException {
        user = new User(username);
        if (username.length() < 2){
            throw new GeneratorException("Generator Exception occurred: username should be at least 2 characters");
        }

        user.setUserId(username);
        user.setLastName ( username.substring(username.length()-2));
        user.setFirstName (username.substring(0, username.length()-2));
        user.setAdministrator(false);
    }

    public User getNext(){
        return this.user;
    }
}
