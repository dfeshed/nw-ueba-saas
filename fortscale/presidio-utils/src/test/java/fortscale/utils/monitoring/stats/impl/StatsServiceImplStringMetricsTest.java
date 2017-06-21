package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.annotations.StatsStringMetricParams;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import org.junit.Assert;
import org.junit.Test;

import static fortscale.utils.monitoring.stats.impl.StatsTestingUtils.engineGroupDataGetStringValueByName;

/**
 * Created by gaashh on 5/30/16.
 */
public class StatsServiceImplStringMetricsTest {

    @StatsMetricsGroupParams(name = "STRING-METRICS")
    static class StringMetrics extends StatsMetricsGroup {

        StringMetrics(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsStringMetricParams
        @StatsStringMetricParams(name = "String-1")
        String string1;

        String nullString;

        @StatsStringMetricParams
        FlexString flexString1;

    }


    // Check string metric field
    @Test()
    public void testStringMetrics() {
        StatsService        statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setManualUpdateMode(true);

        StringMetrics metrics = new StringMetrics(statsService, attributes);


        // --- step 1 ---

        metrics.string1     =  "STR123";
        metrics.flexString1 = new FlexString("FLEX-ABC");

        final long measurementEpoch1 = 10000;
        metrics.manualUpdate(measurementEpoch1);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("STRING-METRICS");
        Assert.assertNotNull(metricsData);

        // Check metrics
        Assert.assertEquals(measurementEpoch1, metricsData.getMeasurementEpoch());

        Assert.assertEquals("STR123", engineGroupDataGetStringValueByName(metricsData, "string1"));
        Assert.assertEquals("STR123", engineGroupDataGetStringValueByName(metricsData, "String-1"));

        Assert.assertNull(engineGroupDataGetStringValueByName(metricsData, "nullString"));

        Assert.assertEquals("FLEX-ABC", engineGroupDataGetStringValueByName(metricsData, "flexString1"));

    }

}
