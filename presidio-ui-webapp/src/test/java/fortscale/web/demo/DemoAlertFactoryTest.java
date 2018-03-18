package fortscale.web.demo;

import fortscale.domain.core.Alert;
import fortscale.domain.core.User;
import fortscale.web.demoservices.DemoAlertFactory;
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
public class DemoAlertFactoryTest {

    @Test
    public void loadUsersTest() throws Exception {
        DemoAlertFactory userFactory = new DemoAlertFactory();
        List<Alert> users = userFactory.getAlerts();
        Assert.assertEquals(322,users.size());

        Assert.assertNotNull(users.get(0));



    }
}
