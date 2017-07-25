package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.user.SingleUserGenerator;
import presidio.data.domain.User;
import presidio.data.generators.user.UserWithoutIdGenerator;

/**
 * Created by cloudera on 6/1/17.
 */
public class UserGeneratorTest {
    @Test
    public void UserGeneratorTest() {
        // first and last name of user constructed from username:
        // last name - from two last username chars, first name - the rest of username
        SingleUserGenerator generator = null;
        try {
            generator = new SingleUserGenerator("dlpuser");
        } catch (GeneratorException e) {
            e.printStackTrace();
        }

        User user = generator.getNext();
        Assert.assertEquals(user.getFirstName(), "dlpus");
        Assert.assertEquals(user.getLastName(), "er");
     }

    @Test (expected = GeneratorException.class)
    public void UserGeneratorExeptionTest() throws GeneratorException {
        SingleUserGenerator generator = new SingleUserGenerator("d");
    }

    @Test
    public void UserWithoutIdTest() throws GeneratorException {
        UserWithoutIdGenerator userGenerator = new UserWithoutIdGenerator("username");
        Assert.assertEquals(userGenerator.getNext().getUserId(),"");
    }
}
