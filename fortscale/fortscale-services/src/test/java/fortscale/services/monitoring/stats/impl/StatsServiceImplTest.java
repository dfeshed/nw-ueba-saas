package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.services.monitoring.stats.StatsMetricsTag;
import fortscale.services.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.services.monitoring.stats.annotations.StatsNumericMetricParams;
import fortscale.services.monitoring.stats.engine.StatsEngine;
import fortscale.services.monitoring.stats.engine.StatsEngineLongMetricData;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.services.monitoring.stats.engine.testing.StatsTestingEngine;

import org.junit.Assert;
import org.junit.Test;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


/**
 * Created by gaashh on 4/3/16.
 */


public class StatsServiceImplTest {


    @StatsMetricsGroupParams(name="TEST-METRICS-ONE-PARAM")
    class TestMetrics1 extends StatsMetricsGroup {

        TestMetrics1(Class cls, StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }

        @StatsNumericMetricParams
        long    longMerticWithoutParameters;

        @StatsNumericMetricParams(name = "long-metric")
        @StatsNumericMetricParams(name = "long-metric-factor", factor = 7.5)
        Long    longMetric;

        @StatsNumericMetricParams
        @StatsNumericMetricParams(name = "float-metric")
        @StatsNumericMetricParams(name = "float-metric-factor", factor = 8.3)
        float   floatMetric;

        // Non-metric-fields
        long             nonMetricLong;
        HashMap<Long,String> nonMetricMap;
    }

    //@StatsMetricsGroupParams
    class TestMetrics2 extends StatsMetricsGroup {

        TestMetrics2(Class cls,  StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }

        @StatsNumericMetricParams
        int    intMetric;
    }

    protected TestMetrics1 createAndInitTestMetrics1(StatsMetricsGroupAttributes attributes){

        TestMetrics1  metrics = new TestMetrics1(StatsServiceImplTest.class, attributes);

        metrics.longMerticWithoutParameters = 111;
        metrics.longMetric                  = 222L;
        metrics.floatMetric                 = 5.7f;
        metrics.nonMetricLong               = 333;

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
        attributes.addTag("foo1","FOO");
        attributes.addTag("foo2","FOO-FOO");
        attributes.addTag("foo3","FOO-FOO-FOO");

        return attributes;
    }

    protected StatsMetricsGroupAttributes createGooAttributes(StatsService statsService) {

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();

        attributes.setStatsService(statsService); // TEST ONLY

        attributes.setGroupName("goo-metrics");
        attributes.addTag("goo1","GOO");
        attributes.addTag("goo2","GOO-GOO");

        return attributes;

    }

    protected StatsService createStatsServiceAndEngine() {
        StatsService statsService = new StatsServiceImpl();
        StatsEngine  statsEngine  = new StatsTestingEngine();
        statsService.registerStatsEngine(statsEngine);
        return statsService;
    }

    // Null if not found
    protected String engineGroupDataGetTagByName(StatsEngineMetricsGroupData groupData, String tagName) {

        List<StatsMetricsTag> tagsList =  groupData.getMetricsTags();

        Optional<StatsMetricsTag> result = tagsList.stream().filter(tag -> tag.getName().equals(tagName)).findFirst();

        if ( ! result.isPresent() ) {
            return null;
        }

        return result.get().getValue();

    }

    // NOT_FOUND (-11223344) if not found
    protected long engineGroupDataGetLongValueByName(StatsEngineMetricsGroupData groupData, String valueName) {

        final long NOT_FOUND = -11223344;

        List<StatsEngineLongMetricData> valuesList =  groupData.getLongMetricsDataList();

        Optional<StatsEngineLongMetricData> result =
                valuesList.stream().filter(tag -> tag.getName().equals(valueName)).findFirst();

        if ( ! result.isPresent() ) {
            return NOT_FOUND;
        }

        return result.get().getValue();

    }


    @Test
    public void basicTest1(){
        StatsService statsService = createStatsServiceAndEngine();
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

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
        Assert.assertEquals(StatsServiceImplTest.class, testMetrics1fooData.getInstrumentedClass() );

        // Check measurementEpoch
        Assert.assertEquals(measurementEpoch, testMetrics1fooData.getMeasurementEpoch() );


        // Check tags
        Assert.assertEquals( "FOO",         engineGroupDataGetTagByName(testMetrics1fooData, "foo1") );
        Assert.assertEquals( "FOO-FOO",     engineGroupDataGetTagByName(testMetrics1fooData, "foo2") );
        Assert.assertEquals( "FOO-FOO-FOO", engineGroupDataGetTagByName(testMetrics1fooData, "foo3") );

        // Check values. TODO: support factor
        Assert.assertEquals( 111, engineGroupDataGetLongValueByName(testMetrics1fooData, "longMerticWithoutParameters") );
        Assert.assertEquals( 222, engineGroupDataGetLongValueByName(testMetrics1fooData, "long-metric") );
        Assert.assertEquals( 222, engineGroupDataGetLongValueByName(testMetrics1fooData, "long-metric-factor") );

        // -- check metric2goo data ---

        // Check group name is set by the attributes (there is no annotation)
        StatsEngineMetricsGroupData testMetrics2gooData= statsEngine.getLatestMetricsGroupData("goo-metrics");
        Assert.assertNotNull(testMetrics2gooData);

        // Check instrumentedClass
        Assert.assertEquals(StatsServiceImplTest.class, testMetrics2gooData.getInstrumentedClass() );

        // Check measurementEpoch
        Assert.assertEquals(measurementEpoch, testMetrics2gooData.getMeasurementEpoch() );

        // Check tags
        Assert.assertEquals( "GOO",         engineGroupDataGetTagByName(testMetrics2gooData, "goo1") );
        Assert.assertEquals( "GOO-GOO",     engineGroupDataGetTagByName(testMetrics2gooData, "goo2") );

        // Check values. TODO: support factor
        Assert.assertEquals( 444, engineGroupDataGetLongValueByName(testMetrics2gooData, "intMetric") );


    }

}
