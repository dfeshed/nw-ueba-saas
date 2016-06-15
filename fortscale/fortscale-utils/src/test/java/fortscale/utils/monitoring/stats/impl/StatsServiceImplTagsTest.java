package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import fortscale.utils.process.hostnameService.HostnameService;
import org.junit.Assert;
import org.junit.Test;



import static fortscale.utils.monitoring.stats.impl.StatsTestingUtils.*;
import static fortscale.utils.monitoring.stats.impl.StatsTestingUtils.engineGroupDataGetTagByName;

/**
 * Created by gaashh on 5/31/16.
 */
public class StatsServiceImplTagsTest {

    @StatsMetricsGroupParams(name = "tags.test")
    static class TagsMetrics extends StatsMetricsGroup {

        TagsMetrics(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsLongMetricParams
        long dummy;

    }


    // Check simple automatic adding of process names and host name
    @Test
    public void testSimpleTags() {
        StatsService       statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("foo", "FOO");
        attributes.addTag("goo", "GOO");
        attributes.setManualUpdateMode(true);

        TagsMetrics metrics = new TagsMetrics(statsService, attributes);

        metrics.manualUpdate(123456789L);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertEquals(TEST_PROCESS_NAME,       engineGroupDataGetTagByName(metricsData, "process"));
        Assert.assertEquals(TEST_PROCESS_GROUP_NAME, engineGroupDataGetTagByName(metricsData, "processGroup"));
        Assert.assertEquals(TEST_HOST_NAME,          engineGroupDataGetTagByName(metricsData, "host"));
    }

    // Check override of automatic adding of process names and host name
    @Test
    public void testOverrideTags() {

        final String OVERRIDE_PROCESS_NAME       = "override-process";
        final String OVERRIDE_PROCESS_GROUP_NAME = "override-process-group";

        StatsService       statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("foo", "FOO");
        attributes.addTag("goo", "GOO");
        attributes.overrideProcessName(OVERRIDE_PROCESS_NAME, OVERRIDE_PROCESS_GROUP_NAME);
        attributes.setManualUpdateMode(true);

        TagsMetrics metrics = new TagsMetrics(statsService, attributes);

        metrics.manualUpdate(123456789L);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertEquals(OVERRIDE_PROCESS_NAME,       engineGroupDataGetTagByName(metricsData, "process"));
        Assert.assertEquals(OVERRIDE_PROCESS_GROUP_NAME, engineGroupDataGetTagByName(metricsData, "processGroup"));
        Assert.assertEquals(TEST_HOST_NAME,              engineGroupDataGetTagByName(metricsData, "host"));
    }

    // Check simple names and host name tag adding while there are tags with the same name
    @Test
    public void testSimpleTagsWithIgnoredTags() {
        StatsService       statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("foo", "FOO");
        attributes.addTag("goo", "GOO");
        attributes.addTag("process",      "process-to-ignore");
        attributes.addTag("processGroup", "process-group-to-ignore");
        attributes.addTag("host",         "host-to-ignore");

        attributes.setManualUpdateMode(true);

        TagsMetrics metrics = new TagsMetrics(statsService, attributes);

        metrics.manualUpdate(123456789L);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertEquals(TEST_PROCESS_NAME,       engineGroupDataGetTagByName(metricsData, "process"));
        Assert.assertEquals(TEST_PROCESS_GROUP_NAME, engineGroupDataGetTagByName(metricsData, "processGroup"));
        Assert.assertEquals(TEST_HOST_NAME,          engineGroupDataGetTagByName(metricsData, "host"));
    }

    // Check null host name service
    @Test
    public void testNullHostnameService() {
        HostnameService hostnameService = null;
        StatsService       statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngineExtended("p","pg",123, hostnameService);
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("foo", "FOO");
        attributes.addTag("goo", "GOO");
        attributes.setManualUpdateMode(true);

        TagsMetrics metrics = new TagsMetrics(statsService, attributes);

        // --- step 1 ---
        metrics.manualUpdate(123456789L);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertNull(engineGroupDataGetTagByName(metricsData, "host"));

        // --- step 1B --- to check 2nd update
        metrics.manualUpdate(123456790L);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertNull(engineGroupDataGetTagByName(metricsData, "host"));

    }

    // Check host name service name change
    @Test
    public void testHostnameServiceNameChange() {

        final String HOST_NAME1 = "host1";
        final String HOST_NAME2 = "host2";
        final String HOST_NAME3 = "host3";

        MockHostnameService hostnameService = new MockHostnameService(HOST_NAME1);
        StatsService       statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngineExtended("p","pg",123, hostnameService);
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("foo", "FOO");
        attributes.addTag("goo", "GOO");
        attributes.setManualUpdateMode(true);

        TagsMetrics metrics = new TagsMetrics(statsService, attributes);

        // --- step 1
        metrics.manualUpdate(123456780L);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertEquals(HOST_NAME1, engineGroupDataGetTagByName(metricsData, "host"));

        // --- step 2
        hostnameService.setHostname(HOST_NAME2);
        metrics.manualUpdate(123456781L);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertEquals(HOST_NAME2, engineGroupDataGetTagByName(metricsData, "host"));

        // --- step 2B - don't change host name
        metrics.manualUpdate(123456781L);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertEquals(HOST_NAME2, engineGroupDataGetTagByName(metricsData, "host"));

        // --- step 3
        hostnameService.setHostname(HOST_NAME3);
        metrics.manualUpdate(123456788L);

        // Get the data
        metricsData = statsEngine.getLatestMetricsGroupData("tags.test");
        Assert.assertNotNull(metricsData);

        // Check the metrics tags
        Assert.assertEquals("FOO", engineGroupDataGetTagByName(metricsData, "foo"));
        Assert.assertEquals("GOO", engineGroupDataGetTagByName(metricsData, "goo"));
        Assert.assertEquals(HOST_NAME3, engineGroupDataGetTagByName(metricsData, "host"));

    }


}
