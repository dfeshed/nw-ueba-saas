package fortscale.web.demo;

import fortscale.domain.core.User;
import fortscale.web.demoservices.DemoEventsFactory;
import fortscale.web.demoservices.DemoUserFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by shays on 23/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DemoIndicatorsFactoryTest {

    @Test
    public void loadUsersTest() throws Exception {
        DemoEventsFactory eventsFactory = new DemoEventsFactory();
        List<Map<String, Object>> events = eventsFactory.getLogonEvents("asuggeyc",1509170400000L,1509174000000L,null);
        Assert.assertEquals(9,events.size());




    }
}
