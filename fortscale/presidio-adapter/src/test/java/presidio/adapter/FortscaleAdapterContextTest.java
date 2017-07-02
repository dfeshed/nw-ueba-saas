package presidio.adapter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.adapter.config.FetchServiceConfig;
import presidio.adapter.configuration.AdapterTestConfiguration;
import presidio.adapter.services.api.AdapterExecutionService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapterTestConfiguration.class)
public class FortscaleAdapterContextTest {

    @Autowired
    private AdapterExecutionService processService;

    @Autowired
    private FetchServiceConfig fetchServiceConfig;

    @Test
    public void contextLoads() throws Exception {
        //processService.run("SHAY");
        Assert.assertNotNull(this.fetchServiceConfig);
    }

}
