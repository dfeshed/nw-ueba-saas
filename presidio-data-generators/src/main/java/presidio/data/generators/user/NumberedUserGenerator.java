package presidio.data.generators.user;

import org.apache.commons.lang3.RandomStringUtils;
import presidio.data.domain.User;
import presidio.data.generators.common.GeneratorException;

public class NumberedUserGenerator implements IUserGenerator {
    private final int totalUserNames;
    private final int userIdPerName;
    private final String namePrefix;
    private final int totalAdmins;
    private int userNameIdx = 1;
    private int userIdPerNameIdx = 1;

    public NumberedUserGenerator() throws GeneratorException {
        totalUserNames = 4000;
        userIdPerName = 30;
        namePrefix = "qa";
        totalAdmins = 100;
    }

    public User getNext(){

        String username = String.format("%s_%06d_%03d", namePrefix, userNameIdx, userIdPerNameIdx);
        User user = new User(username);
        user.setUserId(username);
        user.setLastName ( username.substring(username.length()-2));
        user.setFirstName (username.substring(0, username.length()-2));
        user.setAdministrator(userNameIdx % totalAdmins == 0);

        // set next user counters
        if (userNameIdx >= totalUserNames) {
            userNameIdx = 1;
            userIdPerNameIdx = 1;
        } else if (userIdPerNameIdx >= userIdPerName) {
            userIdPerNameIdx = 1;
        }

        return user;
    }
}
