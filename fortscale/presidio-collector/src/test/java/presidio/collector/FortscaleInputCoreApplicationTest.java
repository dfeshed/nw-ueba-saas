package presidio.collector;

import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.collector.spring.CollectorConfig;

import java.time.Instant;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectorConfig.class)
@Ignore //todo
public class FortscaleInputCoreApplicationTest {

    @Autowired
    private PresidioExecutionService processService;

    public void contextLoads() throws Exception {
        processService.run(DataSource.DLPFILE, Instant.now(), Instant.now(), 36000L);
    }

}
