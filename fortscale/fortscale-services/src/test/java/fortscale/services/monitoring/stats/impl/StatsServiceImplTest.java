package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.services.monitoring.stats.StatsMetricsTag;
import fortscale.services.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.services.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.services.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.services.monitoring.stats.engine.StatsEngine;
import fortscale.services.monitoring.stats.engine.StatsEngineDoubleMetricData;
import fortscale.services.monitoring.stats.engine.StatsEngineLongMetricData;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.services.monitoring.stats.impl.engine.testing.StatsTestingEngine;

import org.junit.Assert;
import org.junit.Test;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsService;
import fortscale.utils.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by gaashh on 4/3/16.
 */


public class StatsServiceImplTest {

    final double epsilon = 0.0000001;


    @StatsMetricsGroupParams(name = "TEST-METRICS-ONE-PARAM")
    class TestMetrics1 extends StatsMetricsGroup {

        TestMetrics1(Class cls, StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }

        @StatsLongMetricParams
        long longMerticWithoutParameters;

        @StatsLongMetricParams(name = "long-metric")
        @StatsDoubleMetricParams(name = "long-as-double-metric")
        @StatsLongMetricParams(name = "long-metric-factor", factor = 7.5)
        Long longMetric;

        @StatsDoubleMetricParams
        @StatsDoubleMetricParams(name = "float-metric")
        @StatsLongMetricParams(name = "float-as-long-metric")
        @StatsDoubleMetricParams(name = "float-metric-factor", factor = 8.3)
        float floatMetric;

        // Non-metric-fields
        long nonMetricLong;
        HashMap<Long, String> nonMetricMap;
    }

    //@StatsMetricsGroupParams
    class TestMetrics2 extends StatsMetricsGroup {

        TestMetrics2(Class cls, StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }

        @StatsLongMetricParams
        int intMetric;
    }

    protected TestMetrics1 createAndInitTestMetrics1(StatsMetricsGroupAttributes attributes) {

        TestMetrics1 metrics = new TestMetrics1(StatsServiceImplTest.class, attributes);

        metrics.longMerticWithoutParameters = 111;
        metrics.longMetric = 222L;
        metrics.floatMetric = 5.7f;
        metrics.nonMetricLong = 333;

        return metrics;
    }

    protected TestMetrics2 createAndInitTestMetrics2(StatsMetricsGroupAttributes attributes) {

        TestMetrics2 metrics = new TestMetrics2(StatsServiceImplTest.class, attributes);

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

    protected StatsService createStatsServiceAndEngine() {
        StatsService statsService = new StatsServiceImpl();
        StatsEngine statsEngine = new StatsTestingEngine();
        statsService.registerStatsEngine(statsEngine);
        return statsService;
    }

    protected StatsMetricsGroupAttributes createStatsServiceAndAttibutes(){

        // Create stats service, an engine and register it
        StatsService statsService = new StatsServiceImpl();
        StatsEngine  statsEngine  = new StatsTestingEngine();
        statsService.registerStatsEngine(statsEngine);

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

    // NOT_FOUND (-11223344) if not found
    protected long engineGroupDataGetLongValueByName(StatsEngineMetricsGroupData groupData, String valueName) {

        final long NOT_FOUND = -11223344;

        List<StatsEngineLongMetricData> valuesList = groupData.getLongMetricsDataList();

        Optional<StatsEngineLongMetricData> result =
                valuesList.stream().filter(tag -> tag.getName().equals(valueName)).findFirst();

        if (!result.isPresent()) {
            return NOT_FOUND;
        }

        return result.get().getValue();

    }

    // NOT_FOUND (-11223344) if not found
    protected double engineGroupDataGetDoubleValueByName(StatsEngineMetricsGroupData groupData, String valueName) {

        final long NOT_FOUND = -11223344;

        List<StatsEngineDoubleMetricData> valuesList = groupData.getDoubleMetricsDataList();

        Optional<StatsEngineDoubleMetricData> result =
                valuesList.stream().filter(tag -> tag.getName().equals(valueName)).findFirst();

        if (!result.isPresent()) {
            return NOT_FOUND;
        }

        return result.get().getValue();

    }


    @Test
    public void basicTest1() {
        StatsService statsService = createStatsServiceAndEngine();
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
        Assert.assertEquals(111, engineGroupDataGetLongValueByName(testMetrics1fooData, "longMerticWithoutParameters"));

        // check longMetric
        Assert.assertEquals(222, engineGroupDataGetLongValueByName(testMetrics1fooData, "long-metric"));
        Assert.assertEquals(222.0, engineGroupDataGetDoubleValueByName(testMetrics1fooData, "long-as-double-metric"), epsilon);
        Assert.assertEquals(222, engineGroupDataGetLongValueByName(testMetrics1fooData, "long-metric-factor")); //  TODO: support factor

        // Check floatMetric
        Assert.assertEquals(5.7f, engineGroupDataGetDoubleValueByName(testMetrics1fooData, "floatMetric"), epsilon);
        Assert.assertEquals(5.7f, engineGroupDataGetDoubleValueByName(testMetrics1fooData, "float-metric"), epsilon);
        Assert.assertEquals(6, engineGroupDataGetLongValueByName(testMetrics1fooData, "float-as-long-metric"));
        Assert.assertEquals(5.7f, engineGroupDataGetDoubleValueByName(testMetrics1fooData, "float-metric"), epsilon); //  TODO: support factor


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

        // Check values. TODO: support factor
        Assert.assertEquals(444, engineGroupDataGetLongValueByName(testMetrics2gooData, "intMetric"));

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
        Assert.assertEquals(1000L, engineGroupDataGetLongValueByName(testMetrics1fooDataUpdated, "long-metric"));

        // Check metrics2goo was not updated
        Assert.assertEquals(measurementEpoch, testMetrics2gooDataUpdated.getMeasurementEpoch());
        Assert.assertEquals(444, engineGroupDataGetLongValueByName(testMetrics2gooDataUpdated, "intMetric"));

    }

    @StatsMetricsGroupParams(name = "SIMPLE-TEST-METRICS")
    static class SimpleTestMetrics extends StatsMetricsGroup {

        SimpleTestMetrics(Class cls, StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }

        @StatsLongMetricParams
        long long1;
    }

    // Check metrics group registration without stats engine
    @Test (expected = StatsMetricsExceptions.NoStatsEngineException.class)
    public void noStatsEngineTest1() {
        StatsService statsService = new StatsServiceImpl();
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setStatsService(statsService);

        // Should throw
        new SimpleTestMetrics(StatsServiceImplTest.class, attributes);
    }

    // Check double stats engine registration
    @Test (expected = StatsMetricsExceptions.StatsEngineAlreadyRegisteredException.class)
    public void doubleStatsEngineRegistrationTest1() {

        StatsService statsService = new StatsServiceImpl();
        StatsEngine  statsEngine  = new StatsTestingEngine();

        // First time - OK
        statsService.registerStatsEngine(statsEngine);

        // 2nd time - fail
        statsService.registerStatsEngine(statsEngine);


    }

    @StatsMetricsGroupParams(name = "DUP-FIELD-TEST")
    static class DuplicatedFieldTestMetrics extends StatsMetricsGroup {

        DuplicatedFieldTestMetrics(Class cls, StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }

        @StatsLongMetricParams // name is the default, the field name
        @StatsLongMetricParams(name="long1")
        long long1;
    }

    // Check duplicated field name
    @Test  (expected = StatsMetricsExceptions.ProblemWhileRegisteringMetricsGroupException.class)
    public void duplicatedFieldName() throws Exception {

        StatsMetricsGroupAttributes attributes = createStatsServiceAndAttibutes();

        // We are interested in the inner exception (the cause). Test in explicitly
        try {
            // Should throw
            new DuplicatedFieldTestMetrics(StatsServiceImplTest.class, attributes);
        }
        catch (Exception ex) {

            // Check the inner exception
            Throwable cause = ex.getCause();
            Assert.assertEquals(StatsMetricsExceptions.MetricNameAlreadyExistsException.class,
                    cause.getClass());

            // Re-throw it to check the external exception
            throw (ex);
        }
    }


    //@Test
    public void printClassPath(){

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            System.out.println(url.getFile());
        }

    }

}