package presidio.adapter;

import fortscale.common.shell.PresidioExecutionService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.adapter.config.FetchServiceConfig;
import presidio.adapter.configuration.AdapterTestConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapterTestConfiguration.class)
@Ignore
public class FortscaleAdapterContextTest {

    @Autowired
    private PresidioExecutionService processService;

    @Autowired
    private FetchServiceConfig fetchServiceConfig;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull(this.fetchServiceConfig);
    }

}
