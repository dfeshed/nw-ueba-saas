package presidio.data.generators.user;

import org.apache.commons.lang3.RandomStringUtils;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.domain.User;

public class RandomUserGenerator implements IUserGenerator {

    public RandomUserGenerator() throws GeneratorException {
    }

    public User getNext(){

        String username = RandomStringUtils.randomAlphanumeric(10);
        User user = new User(username);
        user.setNormalizedUsername(username);
        user.setLastName ( username.substring(username.length()-2));
        user.setFirstName (username.substring(0, username.length()-2));

        return user;
    }
}
