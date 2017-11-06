package presidio.data.generators.user;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.User;
import presidio.data.generators.common.GeneratorException;

public class NumberedUserCyclicGeneratorTest {
    @Test
    public void UserGeneratorTest() {
        NumberedUserCyclicGenerator generator = null;
        try {
            generator = new NumberedUserCyclicGenerator("user", 1000, 4, 10);
        } catch (GeneratorException e) {
            e.printStackTrace();
        }

        User user = generator.getNext();
        Assert.assertEquals(user.getUsername(), "user_000001");
        Assert.assertEquals(user.getUserId(), "user_000001_001");
        System.out.println(user.getUsername() + " " + user.getUserId());
        int isAdminCount = 0;
        if (user.isAdministrator()) isAdminCount++;
        for (int i = 2; i <= 3996; i++)
        {
            user = generator.getNext();
            System.out.println(user.getUsername() + " " + user.getUserId() + (user.isAdministrator()?" admin":""));
            if (user.isAdministrator()) isAdminCount++;
        }

        Assert.assertEquals (396,isAdminCount);

        Assert.assertEquals("user_000999", user.getUsername());
        Assert.assertEquals("user_000999_004", user.getUserId());

        user = generator.getNext();
        Assert.assertEquals("user_001000", user.getUsername());
        Assert.assertEquals("user_001000_001", user.getUserId());
        Assert.assertEquals("user_001000_002",generator.getNext().getUserId());
        generator.getNext();
        generator.getNext();
        Assert.assertEquals(generator.getNext().getUserId(), "user_000001_001");
    }
}
