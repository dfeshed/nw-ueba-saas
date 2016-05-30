package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.annotations.StatsStringMetricParams;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static fortscale.utils.monitoring.stats.impl.StatsTestingUtils.*;

/**
 * Created by gaashh on 4/3/16.
 */


public class StatsServiceImplMiscTest {

    final double epsilon = 0.0000001;


    @StatsMetricsGroupParams(name = "TEST-METRICS-ONE-PARAM")
    class TestMetrics1 extends StatsMetricsGroup {

        TestMetrics1(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsLongMetricParams
        long longMerticWithoutParameters;

        @StatsLongMetricParams(name = "long-metric")
        @StatsDoubleMetricParams(name = "long-as-double-metric")
        Long longMetric;

        @StatsDoubleMetricParams
        @StatsDoubleMetricParams(name = "float-metric")
        @StatsLongMetricParams(name = "float-as-long-metric")
        float floatMetric;

        // Non-metric-fields
        long nonMetricLong;
        HashMap<Long, String> nonMetricMap;
    }

    //@StatsMetricsGroupParams
    class TestMetrics2 extends StatsMetricsGroup {

        TestMetrics2(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsLongMetricParams
        int intMetric;
    }

    protected TestMetrics1 createAndInitTestMetrics1(StatsService statsService, StatsMetricsGroupAttributes attributes) {

        TestMetrics1 metrics = new TestMetrics1(statsService, attributes);

        metrics.longMerticWithoutParameters = 111;
        metrics.longMetric = 222L;
        metrics.floatMetric = 5.7f;
        metrics.nonMetricLong = 333;

        return metrics;
    }

    protected TestMetrics2 createAndInitTestMetrics2(StatsService statsService, StatsMetricsGroupAttributes attributes) {

        TestMetrics2 metrics = new TestMetrics2(statsService, attributes);

        metrics.intMetric = 444;

        return metrics;

    }

    protected StatsMetricsGroupAttributes createFooAttributes() {

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();

        attributes.setGroupName("foo-metrics");
        attributes.addTag("foo1", "FOO");
        attributes.addTag("foo2", "FOO-FOO");
        attributes.addTag("foo3", "FOO-FOO-FOO");

        attributes.setManualUpdateMode(true);

        return attributes;
    }

    protected StatsMetricsGroupAttributes createGooAttributes() {

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();

        attributes.setGroupName("goo-metrics");
        attributes.addTag("goo1", "GOO");
        attributes.addTag("goo2", "GOO-GOO");

        attributes.setManualUpdateMode(true);

        return attributes;

    }


    protected StatsMetricsGroupAttributes createStatsServiceAndAttributes() {

        // Create stats service, an engine and register it
        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();

        // Create attributes and set the stats service
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();

        return attributes;
    }



    @Test
    public void basicTest1() {
        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine = (StatsTestingEngine) statsService.getStatsEngine();

        StatsMetricsGroupAttributes fooAttributes = createFooAttributes();
        StatsMetricsGroupAttributes gooAttributes = createGooAttributes();

        TestMetrics1 testMetrics1foo = createAndInitTestMetrics1(statsService, fooAttributes);
        TestMetrics2 testMetrics2goo = createAndInitTestMetrics2(statsService, gooAttributes);

        final long measurementEpoch = 1234;
        testMetrics1foo.manualUpdate(measurementEpoch);
        testMetrics2goo.manualUpdate(measurementEpoch);

        // -- check metric1foo data ---

        // Check group name is set by the annotation and has priority over the attributes
        StatsEngineMetricsGroupData testMetrics1fooData = statsEngine.getLatestMetricsGroupData("TEST-METRICS-ONE-PARAM");
        Assert.assertNotNull(testMetrics1fooData);

        // Check instrumentedClass
        Assert.assertEquals(StatsServiceImplMiscTest.class, testMetrics1fooData.getInstrumentedClass());

        // Check measurementEpoch
        Assert.assertEquals(measurementEpoch, testMetrics1fooData.getMeasurementEpoch());


        // Check tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(testMetrics1fooData, "foo1"));
        Assert.assertEquals("FOO-FOO", engineGroupDataGetTagByName(testMetrics1fooData, "foo2"));
        Assert.assertEquals("FOO-FOO-FOO", engineGroupDataGetTagByName(testMetrics1fooData, "foo3"));

        // --- Check values metric1foo ---
        // check longMerticWithoutParameters
        Assert.assertEquals((Long) 111L, engineGroupDataGetLongValueByName(testMetrics1fooData, "longMerticWithoutParameters"));

        // check longMetric
        Assert.assertEquals((Long) 222L, engineGroupDataGetLongValueByName(testMetrics1fooData, "long-metric"));
        Assert.assertEquals(222.0, engineGroupDataGetDoubleValueByName(testMetrics1fooData, "long-as-double-metric"), epsilon);

        // Check floatMetric
        Assert.assertEquals(5.7f, engineGroupDataGetDoubleValueByName(testMetrics1fooData, "floatMetric"), epsilon);
        Assert.assertEquals(5.7f, engineGroupDataGetDoubleValueByName(testMetrics1fooData, "float-metric"), epsilon);
        Assert.assertEquals((Long) 6L, engineGroupDataGetLongValueByName(testMetrics1fooData, "float-as-long-metric"));


        // -- check metric2goo data ---

        // Check group name is set by the attributes (there is no annotation)
        StatsEngineMetricsGroupData testMetrics2gooData = statsEngine.getLatestMetricsGroupData("goo-metrics");
        Assert.assertNotNull(testMetrics2gooData);

        // Check instrumentedClass
        Assert.assertEquals(StatsServiceImplMiscTest.class, testMetrics2gooData.getInstrumentedClass());

        // Check measurementEpoch
        Assert.assertEquals(measurementEpoch, testMetrics2gooData.getMeasurementEpoch());

        // Check tags
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(testMetrics2gooData, "goo1"));
        Assert.assertEquals("GOO-GOO", engineGroupDataGetTagByName(testMetrics2gooData, "goo2"));

        // Check values.
        Assert.assertEquals((Long) 444L, engineGroupDataGetLongValueByName(testMetrics2gooData, "intMetric"));

        // --- check manual update. Update metric1foo and metrics2goo but call manualUpdate only for metrics1foo.
        // make sure metric1foo was updated  while metric2goo was not

        // Change some values
        testMetrics1foo.longMetric = 1000L;
        testMetrics2goo.intMetric = 2000;

        // Do manual update only for metrics1foo
        final long measurementEpochUpdated = 2222;
        testMetrics1foo.manualUpdate(measurementEpochUpdated);

        // Get the data
        StatsEngineMetricsGroupData testMetrics1fooDataUpdated = statsEngine.getLatestMetricsGroupData("TEST-METRICS-ONE-PARAM");
        Assert.assertNotNull(testMetrics1fooDataUpdated);

        StatsEngineMetricsGroupData testMetrics2gooDataUpdated = statsEngine.getLatestMetricsGroupData("goo-metrics");
        Assert.assertNotNull(testMetrics2gooDataUpdated);

        // Check metrics1foo was updated
        Assert.assertEquals(measurementEpochUpdated, testMetrics1fooDataUpdated.getMeasurementEpoch());
        Assert.assertEquals((Long) 1000L, engineGroupDataGetLongValueByName(testMetrics1fooDataUpdated, "long-metric"));

        // Check metrics2goo was not updated
        Assert.assertEquals(measurementEpoch, testMetrics2gooDataUpdated.getMeasurementEpoch());
        Assert.assertEquals((Long) 444L, engineGroupDataGetLongValueByName(testMetrics2gooDataUpdated, "intMetric"));

    }

    @StatsMetricsGroupParams(name = "SIMPLE-TEST-METRICS")
    static class SimpleTestMetrics extends StatsMetricsGroup {

        SimpleTestMetrics(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsLongMetricParams
        long long1;
    }


    @StatsMetricsGroupParams(name = "DUP-FIELD-TEST")
    static class DuplicatedFieldTestMetrics extends StatsMetricsGroup {

        DuplicatedFieldTestMetrics(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsLongMetricParams // name is the default, the field name
        @StatsLongMetricParams(name = "long1")
        long long1;
    }

    // Check duplicated field name
    @Test(expected = StatsMetricsExceptions.ProblemWhileRegisteringMetricsGroupException.class)
    public void duplicatedFieldName() {

        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();

        StatsMetricsGroupAttributes attributes = createStatsServiceAndAttributes();

        // We are interested in the inner exception (the cause). Test in explicitly
        try {
            // Should throw
            new DuplicatedFieldTestMetrics(statsService, attributes);
        } catch (RuntimeException ex) {

            // Check the inner exception
            Throwable cause = ex.getCause();
            Assert.assertEquals(StatsMetricsExceptions.MetricNameAlreadyExistsException.class,
                    cause.getClass());

            // Re-throw it to check the external exception
            throw (ex);
        }
    }


    @StatsMetricsGroupParams(name = "EMPTY-METRICS")
    static class EmptyMetrics extends StatsMetricsGroup {

        @StatsLongMetricParams
        Long nullLong;

        @StatsDoubleMetricParams
        Float nullFloat;

        @StatsDateMetricParams
        Long nullDate;

        @StatsStringMetricParams
        String nullStringDate;

        EmptyMetrics(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

    }

    // Check empty metrics group (including with NULL values) is discarded and not passed to the engine
    @Test
    public void testEmptyMetric() {
        StatsService        statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine  statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setManualUpdateMode(true);

        EmptyMetrics metrics = new EmptyMetrics(statsService, attributes);

        metrics.manualUpdate(1122334455);

        // Get the data. Actually, we sould not get the data because empty metrics should be discarded
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("EMPTY-METRICS");
        Assert.assertNull(metricsData);

    }

}