package presidio.data.generators.user;

import org.apache.commons.lang3.RandomStringUtils;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.domain.User;

// by default 2% admin users will be generated
public class RandomAdminUserPercentageGenerator implements IUserGenerator {

    private BooleanPercentageGenerator isAdminGenerator;

    public RandomAdminUserPercentageGenerator() throws GeneratorException {
        isAdminGenerator = new BooleanPercentageGenerator(2);
    }

    public RandomAdminUserPercentageGenerator(int percent) throws GeneratorException {
        isAdminGenerator = new BooleanPercentageGenerator(percent);
    }

    public User getNext(){

        String username = RandomStringUtils.randomAlphanumeric(10);
        User user = new User(username);
        user.setUserId(username);
        user.setLastName ( username.substring(username.length()-2));
        user.setFirstName (username.substring(0, username.length()-2));
        user.setAdministrator(getIsAdminGenerator().getNext());

        return user;
    }

    public BooleanPercentageGenerator getIsAdminGenerator() {
        return isAdminGenerator;
    }

    public void setIsAdminGenerator(BooleanPercentageGenerator isAdminGenerator) {
        this.isAdminGenerator = isAdminGenerator;
    }
}
