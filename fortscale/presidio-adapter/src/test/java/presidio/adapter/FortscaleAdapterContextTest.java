package presidio.adapter;

import fortscale.common.shell.PresidioExecutionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.adapter.spring.AdapterTestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapterTestConfig.class)
public class FortscaleAdapterContextTest {

    @Autowired
    private PresidioExecutionService presidioExecutionService;


    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull(this.presidioExecutionService);
    }
}
