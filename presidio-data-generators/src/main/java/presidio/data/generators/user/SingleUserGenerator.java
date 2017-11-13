package presidio.data.generators.user;

import presidio.data.generators.common.GeneratorException;
import presidio.data.domain.User;

public class SingleUserGenerator implements IUserGenerator {
    private User user;

    public SingleUserGenerator(String username) throws GeneratorException {
        this(username, username,null);
    }

    public SingleUserGenerator(String username, String userId, String email) throws GeneratorException {
        user = new User(username);
        if (username.length() < 2){
            throw new GeneratorException("Generator Exception occurred: username should be at least 2 characters");
        }

        user.setUserId(userId);
        user.setLastName ( username.substring(username.length()-2));
        user.setFirstName (username.substring(0, username.length()-2));
        user.setAdministrator(false);
        user.setEmail(email);
    }

    public SingleUserGenerator(User user, String prefix) throws GeneratorException {
        this(user.getUsername() != null ? prefix + user.getUsername() : null,
                user.getUserId() != null ? prefix + user.getUserId() : null,
                user.getEmail() != null ? prefix + user.getEmail() : null);
    }

    public User getNext(){
        return this.user;
    }
}
