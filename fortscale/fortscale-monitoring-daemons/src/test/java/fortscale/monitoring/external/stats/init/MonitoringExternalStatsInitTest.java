package fortscale.monitoring.external.stats.init;

import fortscale.utils.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/monitoring-external-stats-collector-context-test.xml"})
public class MonitoringExternalStatsInitTest {

    private static final Logger logger = Logger.getLogger(MonitoringExternalStatsInitTest.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMonitoringScheduling() throws Exception {
        InfluxDBStatsInit init = new InfluxDBStatsInit();
        init.createDefaultDB();
    }
}
