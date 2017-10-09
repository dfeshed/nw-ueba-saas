package presidio.data.generators.user;

import presidio.data.domain.User;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.StringRegexCyclicValuesGenerator;

import java.util.Arrays;

/**
 * generates all possible userId values for given regex and stores them in a cyclic array.
 *
 * @see StringRegexCyclicValuesGenerator - for string generation example
 * <p>
 * Created by barak_schuster on 10/8/17.
 */
public class UserRegexCyclicValuesGenerator extends CyclicValuesGenerator<User> implements IUserGenerator {

    public UserRegexCyclicValuesGenerator(String userIdPattern) {
        super(generateUserArr(userIdPattern, "", "", "", false, false));
    }

    public UserRegexCyclicValuesGenerator(String userNamePattern, String userIdPattern, String firstNamePattern, String lastNamePattern, Boolean isAdmin, Boolean isAnonymous) {
        super(generateUserArr(userIdPattern, userNamePattern, firstNamePattern, lastNamePattern, isAdmin, isAnonymous));
    }

    private static User[] generateUserArr(String userIdPattern, String userNamePattern, String firstNamePattern, String lastNamePattern, Boolean isAdmin, Boolean isAnonymous) {
        String[] userIds = new StringRegexCyclicValuesGenerator(userIdPattern).getValues();
        StringRegexCyclicValuesGenerator userNameGenerator = new StringRegexCyclicValuesGenerator(userNamePattern);
        StringRegexCyclicValuesGenerator firstNameGenerator = new StringRegexCyclicValuesGenerator(firstNamePattern);
        StringRegexCyclicValuesGenerator lastNameGenerator = new StringRegexCyclicValuesGenerator(lastNamePattern);

        User[] users = Arrays.stream(userIds).map(id -> {
            String userName = userNameGenerator.getNext();
            String firstName = firstNameGenerator.getNext();
            String lastName = lastNameGenerator.getNext();
            return new User(userName, id, firstName, lastName, isAdmin, isAnonymous);
        }).toArray(User[]::new);
        return users;
    }

}
