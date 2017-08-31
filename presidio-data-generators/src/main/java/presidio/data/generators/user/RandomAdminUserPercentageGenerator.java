package presidio.data.generators.user;

import org.apache.commons.lang3.RandomStringUtils;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IBooleanGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.domain.User;

// by default 2% admin users will be generated
public class RandomAdminUserPercentageGenerator implements IUserGenerator {

    private IBooleanGenerator isAdminGenerator;
    private RandomStringGenerator userNameGenerator;

    public RandomAdminUserPercentageGenerator() throws GeneratorException {
        isAdminGenerator = new BooleanPercentageGenerator(2);
        userNameGenerator = new RandomStringGenerator();
    }

    public RandomAdminUserPercentageGenerator(int percent) throws GeneratorException {
        isAdminGenerator = new BooleanPercentageGenerator(percent);
        userNameGenerator = new RandomStringGenerator();
    }

    public User getNext(){

//        String username = RandomStringUtils.randomAlphanumeric(10);
        String username = userNameGenerator.getNext();
        User user = new User(username);
        user.setUserId(username);
        user.setLastName ( username.substring(username.length()-2));
        user.setFirstName (username.substring(0, username.length()-2));
        user.setAdministrator((Boolean)getIsAdminGenerator().getNext());

        return user;
    }

    public IBooleanGenerator getIsAdminGenerator() {
        return isAdminGenerator;
    }

    public void setIsAdminGenerator(IBooleanGenerator isAdminGenerator) {
        this.isAdminGenerator = isAdminGenerator;
    }
}
