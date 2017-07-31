package presidio.adapter.services.impl;

import fortscale.common.shell.PresidioExecutionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.adapter.spring.AdapterTestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapterTestConfig.class)
public class FlumeAdapterExecutionServiceTest {

    @Autowired
    private PresidioExecutionService presidioExecutionService;

    @Test
    public void name() throws Exception {
//        presidioExecutionService.run();
    }
}
