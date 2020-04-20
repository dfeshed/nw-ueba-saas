package presidio.data.generators.user;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class UserWithoutIdGeneratorTest {
    @Test
    public void UserWithoutIdTest() throws GeneratorException {
        UserWithoutIdGenerator userGenerator = new UserWithoutIdGenerator("username");
        Assert.assertEquals(userGenerator.getNext().getUserId(),"");
    }
}
