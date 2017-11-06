package presidio.data.generators.user;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.User;
import presidio.data.generators.common.GeneratorException;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class NumberedUserGeneratorTest {
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

}
