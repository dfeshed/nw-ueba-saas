package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import org.junit.Assert;
import org.junit.Test;

import static fortscale.utils.monitoring.stats.impl.StatsTestingUtils.engineGroupDataGetDoubleValueByName;

/**
 * Created by gaashh on 5/30/16.
 */
public class StatsServiceImplDoubleMetricsTest {

    @StatsMetricsGroupParams(name = "DOUBLE-METRICS")
    static class DoubleMetrics extends StatsMetricsGroup {

        DoubleMetrics(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsDoubleMetricParams
        long long1;

        @StatsDoubleMetricParams
        Long longObj1;

        @StatsDoubleMetricParams
        int int1;

        @StatsDoubleMetricParams
        Integer intObj1;

        @StatsDoubleMetricParams
        @StatsDoubleMetricParams(name = "Double-1")
        @StatsDoubleMetricParams(name = "Double-1-factor", factor = 44.555)
        @StatsDoubleMetricParams(name = "Double-1-factor-precision", factor = 0.1235678, precisionDigits = 2)
        @StatsDoubleMetricParams(name = "Double-1-factor-rate", factor = 0.2, rateSeconds = 60)
        double double1;

        @StatsDoubleMetricParams
        Double doubleObj1;

        @StatsDoubleMetricParams(name = "doubleNeg-rate-factor-no-negative-rate", factor = 0.2, rateSeconds = 60) // default is negativeRate = false
        @StatsDoubleMetricParams(name = "doubleNeg-rate-factor-negative-rate",    factor = 0.2, rateSeconds = 60, negativeRate = true)
        double doubleNegativeRate;


        @StatsDoubleMetricParams
        float float1;

        @StatsDoubleMetricParams
        Float floatObj1;

        @StatsDoubleMetricParams
        @StatsDoubleMetricParams(name = "double-null-factor", factor = 3.0)
        Double doubleNullObj1;

        @StatsDoubleMetricParams
        FlexLong flexLong1;

        @StatsDoubleMetricParams
        FlexDouble flexDouble1;

    }

    // Check double metric field
    @Test
    public void testDoubleMetrics() {
        StatsService       statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setManualUpdateMode(true);

        DoubleMetrics metrics = new DoubleMetrics(statsService, attributes);

        final double PRECISION = 0.00001;
        final double FLOAT_PRECISION = 0.01;

        // --- step 1 ---

        metrics.long1 = 100L;
        metrics.longObj1 = 200L;
        metrics.int1 = 300;
        metrics.intObj1 = 400;

        metrics.double1 = 1000.41;
        metrics.doubleObj1 = 1100.51;
        metrics.doubleNegativeRate = 1010.41;

        metrics.float1 = 1200.61f;
        metrics.floatObj1 = 1300.49f;

        metrics.doubleNullObj1 = null;

        metrics.flexLong1 = new FlexLong(1400L);
        metrics.flexDouble1 = new FlexDouble(1500.8);

        final long measurementEpoch1 = 10000;
        metrics.manualUpdate(measurementEpoch1);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("DOUBLE-METRICS");
        Assert.assertNotNull(metricsData);

        // Check metrics
        Assert.assertEquals(measurementEpoch1, metricsData.getMeasurementEpoch());

        Assert.assertEquals(100.0, engineGroupDataGetDoubleValueByName(metricsData, "long1").doubleValue(), PRECISION);
        Assert.assertEquals(200.0, engineGroupDataGetDoubleValueByName(metricsData, "longObj1").doubleValue(), PRECISION);
        Assert.assertEquals(300.0, engineGroupDataGetDoubleValueByName(metricsData, "int1").doubleValue(), PRECISION);
        Assert.assertEquals(400.0, engineGroupDataGetDoubleValueByName(metricsData, "intObj1").doubleValue(), PRECISION);

        Assert.assertEquals(1000.41, engineGroupDataGetDoubleValueByName(metricsData, "double1").doubleValue(), PRECISION);
        Assert.assertEquals(1000.41, engineGroupDataGetDoubleValueByName(metricsData, "Double-1").doubleValue(), PRECISION);

        Assert.assertEquals(1100.51, engineGroupDataGetDoubleValueByName(metricsData, "doubleObj1").doubleValue(), PRECISION);
        Assert.assertEquals(1200.61, engineGroupDataGetDoubleValueByName(metricsData, "float1").doubleValue(), FLOAT_PRECISION);
        Assert.assertEquals(1300.49, engineGroupDataGetDoubleValueByName(metricsData, "floatObj1").doubleValue(), FLOAT_PRECISION);

        Assert.assertNull(engineGroupDataGetDoubleValueByName(metricsData, "doubleNullObj1"));
        Assert.assertNull(engineGroupDataGetDoubleValueByName(metricsData, "double-null-factor"));

        Assert.assertEquals(1400.0, engineGroupDataGetDoubleValueByName(metricsData, "flexLong1").doubleValue(), PRECISION);
        Assert.assertEquals(1500.8, engineGroupDataGetDoubleValueByName(metricsData, "flexDouble1").doubleValue(), PRECISION);

        Assert.assertEquals(metrics.double1 * 44.555, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor").doubleValue(), PRECISION);
        Assert.assertEquals(123.62, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor-precision").doubleValue(), PRECISION);

        // rate metric should not exist on first update
        Assert.assertNull(engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor-rate"));
        Assert.assertNull(engineGroupDataGetDoubleValueByName(metricsData, "doubleNeg-rate-factor-no-negative-rate"));
        Assert.assertNull(engineGroupDataGetDoubleValueByName(metricsData, "doubleNeg-rate-factor-negative-rate"));


        // --- step 2 ---
        final long measurementEpoch2 = measurementEpoch1 + 60 * 17;
        metrics.double1            +=  240 / 60 * (measurementEpoch2 - measurementEpoch1);
        metrics.doubleNegativeRate += -240 / 60 * (measurementEpoch2 - measurementEpoch1);

        metrics.manualUpdate(measurementEpoch2);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("DOUBLE-METRICS");
        Assert.assertNotNull(metricsData);

        // check data
        Assert.assertEquals(metrics.double1, engineGroupDataGetDoubleValueByName(metricsData, "double1").doubleValue(), PRECISION);
        Assert.assertEquals(metrics.double1 * 44.555, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor").doubleValue(), PRECISION);

        Assert.assertEquals( -3.0     , engineGroupDataGetDoubleValueByName(metricsData, "doubleNeg-rate-factor-no-negative-rate").doubleValue(), PRECISION);
        Assert.assertEquals(-240 * 0.2, engineGroupDataGetDoubleValueByName(metricsData, "doubleNeg-rate-factor-negative-rate").doubleValue(), PRECISION);

        // --- step 3 ---
        final long measurementEpoch3 = measurementEpoch2 + 60 / 2;  // half a minute
        metrics.double1            +=  180 / 60 * (measurementEpoch3 - measurementEpoch2); // rate per minute 180
        metrics.doubleNegativeRate += -180 / 60 * (measurementEpoch3 - measurementEpoch2); // rate per minute 180

        metrics.manualUpdate(measurementEpoch3);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("DOUBLE-METRICS");
        Assert.assertNotNull(metricsData);

        // check data
        Assert.assertEquals(metrics.double1, engineGroupDataGetDoubleValueByName(metricsData, "double1").doubleValue(), PRECISION);
        Assert.assertEquals(metrics.double1 * 44.555, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor").doubleValue(), PRECISION);
        Assert.assertEquals( 180 * 0.2, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor-rate").doubleValue(), PRECISION);

        Assert.assertEquals( -3.0     , engineGroupDataGetDoubleValueByName(metricsData, "doubleNeg-rate-factor-no-negative-rate").doubleValue(), PRECISION);
        Assert.assertEquals(-180 * 0.2, engineGroupDataGetDoubleValueByName(metricsData, "doubleNeg-rate-factor-negative-rate").doubleValue(), PRECISION);

    }

}
