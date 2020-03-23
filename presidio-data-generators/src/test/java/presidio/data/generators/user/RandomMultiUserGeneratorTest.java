package presidio.data.generators.user;

import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.User;
import presidio.data.generators.common.GeneratorException;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class RandomMultiUserGeneratorTest {
    static Logger LOGGER = (Logger) LoggerFactory.getLogger(RandomMultiUserGeneratorTest.class);

    @Test
    public void multiUserTest() throws GeneratorException {
        RandomMultiUserGenerator.UserGeneratorProbability userGeneratorProbability1 = new RandomMultiUserGenerator.UserGeneratorProbability(new NumberedUserCyclicGenerator("regular_user", 100, 10,0), 1);
        RandomMultiUserGenerator.UserGeneratorProbability userGeneratorProbability2 = new RandomMultiUserGenerator.UserGeneratorProbability(new SingleAdminUserGenerator("admin_user"), 0.1);
        RandomMultiUserGenerator.UserGeneratorProbability userGeneratorProbability3 = new RandomMultiUserGenerator.UserGeneratorProbability(new AnonymousSingleUserGenerator("anonymous_usr"), 0.01);

        List< RandomMultiUserGenerator.UserGeneratorProbability > probabilityList = Arrays.asList(userGeneratorProbability1, userGeneratorProbability2, userGeneratorProbability3);

        RandomMultiUserGenerator generator = new RandomMultiUserGenerator( probabilityList);

        int admins = 0;
        int anonymous = 0;
        for (int i = 0; i< 100; i++) {
            User user = generator.getNext();
            System.out.print(user.getUserId());
            if (user.getAdministrator())
            {
                LOGGER.info(" admin");
                admins++;
            }
            if (user.getAnonymous()) {
                LOGGER.info(" anonymous");
                anonymous++;
            }
            LOGGER.info("");
        }
        Assert.assertEquals(admins,14);
        Assert.assertEquals(anonymous,2);
    }
}
