package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsService;


/**
 * Created by gaashh on 4/3/16.
 */


public class StatsServiceImplTest {

    class TestMetrics extends StatsMetricsGroup {

        TestMetrics(Class cls, StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }


        long x;
        long y;
    }


    TestMetrics testMetrics;

    @Test
    public void StatsServiceRegisterMetricsTest() {

        StatsService statsService = new StatsServiceImpl();
        StatsMetricsGroupAttributes groupAttributes = new StatsMetricsGroupAttributes();
        groupAttributes.setStatsService(statsService);

        testMetrics = new TestMetrics(StatsServiceImplTest.class, groupAttributes);


//        statsService.registerStatsMetricsGroup(testMetrics, StatsServiceImplTest.class);
//        testMetrics.update();

        assertTrue(true);
    }
}
