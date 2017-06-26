package presidio.collector;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.collector.config.FetchServiceConfig;
import presidio.collector.configuration.CollectorTestConfiguration;
import presidio.collector.services.api.CollectorExecutionService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectorTestConfiguration.class)
public class FortscaleCollectorContextTest {

    @Autowired
    private CollectorExecutionService processService;

    @Autowired
    private FetchServiceConfig fetchServiceConfig;

    @Test
    public void contextLoads() throws Exception {
        //processService.run("SHAY");
        Assert.assertNotNull(this.fetchServiceConfig);
    }

}
