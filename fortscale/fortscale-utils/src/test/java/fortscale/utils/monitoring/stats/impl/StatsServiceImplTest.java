package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsDoubleFlexMetric;
import fortscale.utils.monitoring.stats.StatsLongFlexMetric;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsMetricsTag;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.engine.StatsEngine;
import fortscale.utils.monitoring.stats.engine.StatsEngineDoubleMetricData;
import fortscale.utils.monitoring.stats.engine.StatsEngineLongMetricData;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gaashh on 4/3/16.
 */


public class StatsServiceImplTest {

    final double epsilon = 0.0000001;


    @StatsMetricsGroupParams(name = "TEST-METRICS-ONE-PARAM")
    class TestMetrics1 extends StatsMetricsGroup {

        TestMetrics1(StatsMetricsGroupAttributes attributes) {
            super(StatsServiceImplTest.class, attributes);
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

        TestMetrics2(StatsMetricsGroupAttributes attributes) {
            super(StatsServiceImplTest.class, attributes);
        }

        @StatsLongMetricParams
        int intMetric;
    }

    protected TestMetrics1 createAndInitTestMetrics1(StatsMetricsGroupAttributes attributes) {

        TestMetrics1 metrics = new TestMetrics1(attributes);

        metrics.longMerticWithoutParameters = 111;
        metrics.longMetric = 222L;
        metrics.floatMetric = 5.7f;
        metrics.nonMetricLong = 333;

        return metrics;
    }

    protected TestMetrics2 createAndInitTestMetrics2(StatsMetricsGroupAttributes attributes) {

        TestMetrics2 metrics = new TestMetrics2(attributes);

        metrics.intMetric = 444;

        return metrics;

    }

    protected StatsMetricsGroupAttributes createFooAttributes(StatsService statsService) {

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();

        attributes.setStatsService(statsService);  // TEST ONLY

        attributes.setGroupName("foo-metrics");
        attributes.addTag("foo1", "FOO");
        attributes.addTag("foo2", "FOO-FOO");
        attributes.addTag("foo3", "FOO-FOO-FOO");

        return attributes;
    }

    protected StatsMetricsGroupAttributes createGooAttributes(StatsService statsService) {

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();

        attributes.setStatsService(statsService); // TEST ONLY

        attributes.setGroupName("goo-metrics");
        attributes.addTag("goo1", "GOO");
        attributes.addTag("goo2", "GOO-GOO");

        return attributes;

    }


    protected StatsMetricsGroupAttributes createStatsServiceAndAttributes() {

        // Create stats service, an engine and register it
        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();

        // Create attributes and set the stats service
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setStatsService(statsService);

        return attributes;
    }


    // Null if not found
    protected String engineGroupDataGetTagByName(StatsEngineMetricsGroupData groupData, String tagName) {

        List<StatsMetricsTag> tagsList = groupData.getMetricsTags();

        Optional<StatsMetricsTag> result = tagsList.stream().filter(tag -> tag.getName().equals(tagName)).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get().getValue();

    }

    // Null if not found
    protected Long engineGroupDataGetLongValueByName(StatsEngineMetricsGroupData groupData, String valueName) {

        List<StatsEngineLongMetricData> valuesList = groupData.getLongMetricsDataList();

        Optional<StatsEngineLongMetricData> result =
                valuesList.stream().filter(tag -> tag.getName().equals(valueName)).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get().getValue();

    }

    // Null if not found
    protected Double engineGroupDataGetDoubleValueByName(StatsEngineMetricsGroupData groupData, String valueName) {

        List<StatsEngineDoubleMetricData> valuesList = groupData.getDoubleMetricsDataList();

        Optional<StatsEngineDoubleMetricData> result =
                valuesList.stream().filter(tag -> tag.getName().equals(valueName)).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get().getValue();

    }


    @Test
    public void basicTest1() {
        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine = (StatsTestingEngine) statsService.getStatsEngine();

        StatsMetricsGroupAttributes fooAttributes = createFooAttributes(statsService);
        StatsMetricsGroupAttributes gooAttributes = createGooAttributes(statsService);

        TestMetrics1 testMetrics1foo = createAndInitTestMetrics1(fooAttributes);
        TestMetrics2 testMetrics2goo = createAndInitTestMetrics2(gooAttributes);

        final long measurementEpoch = 1234;
        statsService.writeMetricsGroupsToEngine(measurementEpoch);

        // -- check metric1foo data ---

        // Check group name is set by the annotation and has priority over the attributes
        StatsEngineMetricsGroupData testMetrics1fooData = statsEngine.getLatestMetricsGroupData("TEST-METRICS-ONE-PARAM");
        Assert.assertNotNull(testMetrics1fooData);

        // Check instrumentedClass
        Assert.assertEquals(StatsServiceImplTest.class, testMetrics1fooData.getInstrumentedClass());

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
        Assert.assertEquals(StatsServiceImplTest.class, testMetrics2gooData.getInstrumentedClass());

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

        SimpleTestMetrics(StatsMetricsGroupAttributes attributes) {
            super(StatsServiceImplTest.class, attributes);
        }

        @StatsLongMetricParams
        long long1;
    }


    @StatsMetricsGroupParams(name = "DUP-FIELD-TEST")
    static class DuplicatedFieldTestMetrics extends StatsMetricsGroup {

        DuplicatedFieldTestMetrics(StatsMetricsGroupAttributes attributes) {
            super(StatsServiceImplTest.class, attributes);
        }

        @StatsLongMetricParams // name is the default, the field name
        @StatsLongMetricParams(name = "long1")
        long long1;
    }

    // Check duplicated field name
    @Test(expected = StatsMetricsExceptions.ProblemWhileRegisteringMetricsGroupException.class)
    public void duplicatedFieldName() {

        StatsMetricsGroupAttributes attributes = createStatsServiceAndAttributes();

        // We are interested in the inner exception (the cause). Test in explicitly
        try {
            // Should throw
            new DuplicatedFieldTestMetrics(attributes);
        } catch (RuntimeException ex) {

            // Check the inner exception
            Throwable cause = ex.getCause();
            Assert.assertEquals(StatsMetricsExceptions.MetricNameAlreadyExistsException.class,
                    cause.getClass());

            // Re-throw it to check the external exception
            throw (ex);
        }
    }


    // Long flex metric helper class
    class FlexLong implements StatsLongFlexMetric {
        Long value;

        FlexLong(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }
    }

    // Dou flex metric helper class
    class FlexDouble implements StatsDoubleFlexMetric {
        Double value;

        FlexDouble(Double value) {
            this.value = value;
        }

        public Double getValue() {
            return value;
        }
    }


    @StatsMetricsGroupParams(name = "LONG-METRICS")
    static class LongMetrics extends StatsMetricsGroup {

        LongMetrics(StatsMetricsGroupAttributes attributes) {
            super(StatsServiceImplTest.class, attributes);
        }

        @StatsLongMetricParams
        @StatsLongMetricParams(name = "Long-1")
        @StatsLongMetricParams(name = "Long-1-factor", factor = 100)
        @StatsLongMetricParams(name = "Long-1-factor-rate", factor = 0.2, rateSeconds = 60)
        long long1;

        @StatsLongMetricParams
        Long longObj1;

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
        StatsTestingEngine  statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setStatsService(statsService);

        LongMetrics metrics = new LongMetrics(attributes);

        //final double precision = 0.00001;

        // --- step 1 ---

        metrics.long1 = 100L;
        metrics.longObj1 = 200L;
        metrics.int1 = 300;
        metrics.intObj1 = 400;

        metrics.double1 = 1000.41;
        metrics.doubleObj1 = 1100.51;
        metrics.float1 = 1200.61f;
        metrics.floatObj1 = 1300.49f;

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

        // --- step 2 ---
        final long measurementEpoch2 = measurementEpoch1 + 60 * 17;
        metrics.long1 += 240 / 60 * (measurementEpoch2 - measurementEpoch1);
        metrics.manualUpdate(measurementEpoch2);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("LONG-METRICS");
        Assert.assertNotNull(metricsData);

        // check data
        Assert.assertEquals((Long) metrics.long1, engineGroupDataGetLongValueByName(metricsData, "long1"));
        Assert.assertEquals((Long) (metrics.long1 * 100), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor"));
        Assert.assertEquals((Long) Math.round(240 * 0.2), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor-rate"));

        // --- step 3 ---
        final long measurementEpoch3 = measurementEpoch2 + 60 / 2;  // half a minute
        metrics.long1 += 180 / 60 * (measurementEpoch3 - measurementEpoch2); // rate per minute 180
        metrics.manualUpdate(measurementEpoch3);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("LONG-METRICS");
        Assert.assertNotNull(metricsData);

        // check data
        Assert.assertEquals((Long) metrics.long1, engineGroupDataGetLongValueByName(metricsData, "long1"));
        Assert.assertEquals((Long) (metrics.long1 * 100), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor"));
        Assert.assertEquals((Long) Math.round(180 * 0.2), engineGroupDataGetLongValueByName(metricsData, "Long-1-factor-rate"));

    }

    @StatsMetricsGroupParams(name = "DOUBLE-METRICS")
    static class DoubleMetrics extends StatsMetricsGroup {

        DoubleMetrics(StatsMetricsGroupAttributes attributes) {
            super(StatsServiceImplTest.class, attributes);
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
        StatsService        statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine  statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setStatsService(statsService);

        DoubleMetrics metrics = new DoubleMetrics(attributes);

        final double PRECISION = 0.00001;
        final double FLOAT_PRECISION = 0.01;

        // --- step 1 ---

        metrics.long1 = 100L;
        metrics.longObj1 = 200L;
        metrics.int1 = 300;
        metrics.intObj1 = 400;

        metrics.double1 = 1000.41;
        metrics.doubleObj1 = 1100.51;
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
        Assert.assertNull(engineGroupDataGetDoubleValueByName(metricsData, "DOUBLE-1-factor-rate"));

        // --- step 2 ---
        final long measurementEpoch2 = measurementEpoch1 + 60 * 17;
        metrics.double1 += 240 / 60 * (measurementEpoch2 - measurementEpoch1);
        metrics.manualUpdate(measurementEpoch2);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("DOUBLE-METRICS");
        Assert.assertNotNull(metricsData);

        // check data
        Assert.assertEquals(metrics.double1, engineGroupDataGetDoubleValueByName(metricsData, "double1").doubleValue(), PRECISION);
        Assert.assertEquals(metrics.double1 * 44.555, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor").doubleValue(), PRECISION);
        Assert.assertEquals(240 * 0.2, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor-rate").doubleValue(), PRECISION);

        // --- step 3 ---
        final long measurementEpoch3 = measurementEpoch2 + 60 / 2;  // half a minute
        metrics.double1 += 180 / 60 * (measurementEpoch3 - measurementEpoch2); // rate per minute 180
        metrics.manualUpdate(measurementEpoch3);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("DOUBLE-METRICS");
        Assert.assertNotNull(metricsData);

        // check data
        Assert.assertEquals(metrics.double1, engineGroupDataGetDoubleValueByName(metricsData, "double1").doubleValue(), PRECISION);
        Assert.assertEquals(metrics.double1 * 44.555, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor").doubleValue(), PRECISION);
        Assert.assertEquals(180 * 0.2, engineGroupDataGetDoubleValueByName(metricsData, "Double-1-factor-rate").doubleValue(), PRECISION);

    }


    //@Test
    public void printClassPath() {

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            System.out.println(url.getFile());
        }

    }

}