package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static fortscale.utils.monitoring.stats.impl.StatsTestingUtils.engineGroupDataGetLongValueByName;

/**
 * Created by gaashh on 5/30/16.
 */
public class StatsServiceImplLongMetricsTest {


    @StatsMetricsGroupParams(name = "LONG-METRICS")
    static class LongMetrics extends StatsMetricsGroup {

        LongMetrics(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsLongMetricParams
        @StatsLongMetricParams(name = "Long-1")
        @StatsLongMetricParams(name = "Long-1-factor", factor = 100)
        @StatsLongMetricParams(name = "Long-1-factor-rate", factor = 0.2, rateSeconds = 60)
        long long1;

        @StatsLongMetricParams
        Long longObj1;

        @StatsLongMetricParams(name = "longNeg-Rate-factor-no-negative-rate", factor = 0.2, rateSeconds = 60) // default is negativeRate = false
        @StatsLongMetricParams(name = "longNeg-Rate-factor-negative-rate",    factor = 0.2, rateSeconds = 60, negativeRate = true)
        long longNegativeRate;



        @StatsLongMetricParams
        int int1;

        @StatsLongMetricParams
        Integer intObj1;

        @StatsLongMetricParams
        double double1;

        @StatsLongMetricParams
        Double doubleObj1;

        @StatsLongMetricParams
        float float1;

        @StatsLongMetricParams
        Float floatObj1;

        @StatsLongMetricParams
        @StatsLongMetricParams(name = "long-null-factor", factor = 3.0)
        Long longNullObj1;

        @StatsLongMetricParams
        FlexLong flexLong1;

        @StatsLongMetricParams
        FlexDouble flexDouble1;

        @StatsLongMetricParams
        AtomicLong atomicLong1;

        @StatsLongMetricParams
        AtomicInteger atomicInteger1;

    }


    // Check long metric field
    @Test
    public void testLongMetrics() {
        StatsService        statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setManualUpdateMode(true);

        LongMetrics metrics = new LongMetrics(statsService, attributes);

        //final double precision = 0.00001;

        // --- step 1 ---

        metrics.long1            = 100L;
        metrics.longObj1         = 200L;
        metrics.longNegativeRate = 150L;

        metrics.int1       = 300;
        metrics.intObj1    = 400;

        metrics.double1    = 1000.41;
        metrics.doubleObj1 = 1100.51;
        metrics.float1     = 1200.61f;
        metrics.floatObj1  = 1300.49f;

        metrics.longNullObj1   = null;

        metrics.flexLong1      = new FlexLong(1400L);
        metrics.flexDouble1    = new FlexDouble(1500.8);

        metrics.atomicLong1    = new AtomicLong(1600);
        metrics.atomicInteger1 = new AtomicInteger(1700);

        final long measurementEpoch1 = 10000;
        metrics.manualUpdate(measurementEpoch1);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("LONG-METRICS");
        Assert.assertNotNull(metricsData);

        // Check metrics
        Assert.assertEquals(measurementEpoch1, metricsData.getMeasurementEpoch());

        Assert.assertEquals((Long) 100L, engineGroupDataGetLongValueByName(metricsData, "long1"));
        Assert.assertEquals((Long) 100L, engineGroupDataGetLongValueByName(metricsData, "Long-1"));
        Assert.assertEquals((Long) 200L, engineGroupDataGetLongValueByName(metricsData, "longObj1"));
        Assert.assertEquals((Long) 300L, engineGroupDataGetLongValueByName(metricsData, "int1"));
        Assert.assertEquals((Long) 400L, engineGroupDataGetLongValueByName(metricsData, "intObj1"));

        Assert.assertEquals((Long) 1000L, engineGroupDataGetLongValueByName(metricsData, "double1"));
        Assert.assertEquals((Long) 1101L, engineGroupDataGetLongValueByName(metricsData, "doubleObj1"));
        Assert.assertEquals((Long) 1201L, engineGroupDataGetLongValueByName(metricsData, "float1"));
        Assert.assertEquals((Long) 1300L, engineGroupDataGetLongValueByName(metricsData, "floatObj1"));

        Assert.assertNull(engineGroupDataGetLongValueByName(metricsData, "longNullObj1"));
        Assert.assertNull(engineGroupDataGetLongValueByName(metricsData, "long-null-factor"));

        Assert.assertEquals((Long) 1400L, engineGroupDataGetLongValueByName(metricsData, "flexLong1"));
        Assert.assertEquals((Long) 1501L, engineGroupDataGetLongValueByName(metricsData, "flexDouble1"));

        Assert.assertEquals((Long) 1600L, engineGroupDataGetLongValueByName(metricsData, "atomicLong1"));
        Assert.assertEquals((Long) 1700L, engineGroupDataGetLongValueByName(metricsData, "atomicInteger1"));


        Assert.assertEquals((Long) (100L * 100), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor"));
        // rate metric should not exist on first update
        Assert.assertNull(engineGroupDataGetLongValueByName(metricsData, "Long-1-factor-rate"));
        Assert.assertNull(engineGroupDataGetLongValueByName(metricsData, "longNeg-Rate-factor-no-negative-rate"));
        Assert.assertNull(engineGroupDataGetLongValueByName(metricsData, "longNeg-Rate-factor-negative-rate"));


        // --- step 2 ---
        final long measurementEpoch2 = measurementEpoch1 + 60 * 17;
        metrics.long1            +=  240 / 60 * (measurementEpoch2 - measurementEpoch1);
        metrics.longNegativeRate += -240 / 60 * (measurementEpoch2 - measurementEpoch1);

        metrics.manualUpdate(measurementEpoch2);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("LONG-METRICS");
        Assert.assertNotNull(metricsData);

        // check data
        Assert.assertEquals((Long) metrics.long1, engineGroupDataGetLongValueByName(metricsData, "long1"));
        Assert.assertEquals((Long) (metrics.long1 * 100), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor"));

        Assert.assertEquals((Long) Math.round(240 * 0.2), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor-rate"));

        Assert.assertEquals( new Long(-3L)                , engineGroupDataGetLongValueByName(metricsData, "longNeg-Rate-factor-no-negative-rate"));
        Assert.assertEquals((Long) Math.round( -240 * 0.2), engineGroupDataGetLongValueByName(metricsData, "longNeg-Rate-factor-negative-rate"));


        // --- step 3 ---
        final long measurementEpoch3 = measurementEpoch2 + 60 / 2;  // half a minute
        metrics.long1            +=  180 / 60 * (measurementEpoch3 - measurementEpoch2); // rate per minute 180
        metrics.longNegativeRate += -180 / 60 * (measurementEpoch3 - measurementEpoch2); // rate per minute 180

        metrics.manualUpdate(measurementEpoch3);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("LONG-METRICS");
        Assert.assertNotNull(metricsData);

        // check data
        Assert.assertEquals((Long) metrics.long1, engineGroupDataGetLongValueByName(metricsData, "long1"));
        Assert.assertEquals((Long) (metrics.long1 * 100), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor"));

        Assert.assertEquals((Long) Math.round(180 * 0.2), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor-rate"));

        Assert.assertEquals( new Long(-3L)                , engineGroupDataGetLongValueByName(metricsData, "longNeg-Rate-factor-no-negative-rate"));
        Assert.assertEquals((Long) Math.round( -180 * 0.2), engineGroupDataGetLongValueByName(metricsData, "longNeg-Rate-factor-negative-rate"));

    }

}
