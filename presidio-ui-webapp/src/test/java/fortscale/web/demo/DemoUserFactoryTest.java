package fortscale.web.demo;

import fortscale.domain.core.User;
import fortscale.web.demoservices.DemoUserFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

/**
 * Created by shays on 23/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DemoUserFactoryTest {

    @Test
    public void loadUsersTest() throws Exception {
        DemoUserFactory userFactory = new DemoUserFactory();
        List<User> users = userFactory.getUsers();
        Assert.assertEquals(911,users.size());

        Assert.assertNotNull(users.get(0));
        Assert.assertTrue(users.get(0).getId().length()>0);
        Assert.assertTrue(users.get(0).getUsername().length()>0);
        Assert.assertTrue(users.get(0).getScore()>0);
        Assert.assertTrue(!users.get(0).getTags().contains("admin"));



    }
}
