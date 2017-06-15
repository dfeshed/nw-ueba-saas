package presidio.collector;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.collector.services.api.CollectorExecutionService;
import presidio.collector.spring.CollectorConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectorConfig.class)
@Ignore //todo
public class FortscaleInputCoreApplicationTest {

    @Autowired
    private CollectorExecutionService processService;

    @Test
    public void contextLoads() throws Exception {
        processService.run("SHAY");
    }

}
