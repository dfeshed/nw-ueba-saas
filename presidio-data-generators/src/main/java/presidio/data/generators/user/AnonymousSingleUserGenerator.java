package presidio.data.generators.user;

import presidio.data.domain.User;
import presidio.data.generators.common.GeneratorException;

public class AnonymousSingleUserGenerator implements IUserGenerator {
    private User user;

    public AnonymousSingleUserGenerator(String username) throws GeneratorException {
        user = new User(username);
        if (username.length() < 2){
            throw new GeneratorException("Generator Exception occurred: username should be at least 2 characters");
        }

        user.setUserId(username);
        user.setLastName ("");
        user.setFirstName ("");
        user.setAdministrator(false);
        user.setAnonymous(true);
    }

    public User getNext(){
        return this.user;
    }
}
