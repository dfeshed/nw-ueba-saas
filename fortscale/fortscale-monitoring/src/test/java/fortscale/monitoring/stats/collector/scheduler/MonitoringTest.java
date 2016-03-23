package fortscale.monitoring.stats.collector.scheduler;

import fortscale.utils.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Test class for Monitoring module
 *
 * @author gils
 * 21/03/2016
 */
public class MonitoringTest {

    private static final Logger logger = Logger.getLogger(MonitoringTest.class);

    @BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/monitoring-context-test.xml");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMonitoringScheduling() throws Exception {
        Assert.assertTrue(true);
    }
}
