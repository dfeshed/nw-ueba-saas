package presidio.data.generators.user;

import presidio.data.domain.User;
import presidio.data.generators.common.GeneratorException;

/** Generates requested number of unique users,
 *  for each user creates requested number of unique IDs,
 *  requested number of users can be Administrators**/
public class NumberedUserCyclicGenerator implements IUserGenerator {
    private final int totalUserNames;
    private final int userIdPerName;
    private final String namePrefix;
    private final int totalAdmins;
    private int userNameIdx = 1;
    private int userIdPerNameIdx = 1;

    public NumberedUserCyclicGenerator() throws GeneratorException {
        totalUserNames = 4000;
        userIdPerName = 30;
        namePrefix = "qa";
        totalAdmins = 100;
    }
   public NumberedUserCyclicGenerator(String namePrefix, int totalUserNames, int userIdPerName, int totalAdmins) throws GeneratorException {
        this.totalUserNames = totalUserNames;
        this.userIdPerName = userIdPerName;
        this.namePrefix = namePrefix;
        this.totalAdmins = totalAdmins;
    }

    public User getNext(){

        String userName = String.format("%s_%06d", namePrefix, userNameIdx);
        String userId = String.format("%s_%06d_%03d", namePrefix, userNameIdx, userIdPerNameIdx);
        User user = new User(userName);
        user.setUserId(userId);
        user.setLastName ( userName.substring(userName.length()-2));
        user.setFirstName (userName.substring(0, userName.length()-2));
        user.setAdministrator(totalAdmins>0 && userNameIdx % totalAdmins == 0);

        // set next user counters
        if (userIdPerNameIdx >= userIdPerName) {
            userIdPerNameIdx = 1;
            userNameIdx++;
        } else { userIdPerNameIdx++; }

        if (userNameIdx > totalUserNames) {
            userNameIdx = 1;
            userIdPerNameIdx = 1;
        }

        return user;
    }
}
