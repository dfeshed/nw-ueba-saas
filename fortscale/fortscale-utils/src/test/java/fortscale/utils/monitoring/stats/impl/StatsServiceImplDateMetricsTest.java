package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static fortscale.utils.monitoring.stats.impl.StatsTestingUtils.engineGroupDataGetLongValueByName;

/**
 * Created by gaashh on 5/30/16.
 */
public class StatsServiceImplDateMetricsTest {

    @StatsMetricsGroupParams(name = "DATE-METRICS")
    static class DateMetrics extends StatsMetricsGroup {

        DateMetrics(StatsService statsService, StatsMetricsGroupAttributes attributes) {
            super(statsService, StatsServiceImplMiscTest.class, attributes);
        }

        @StatsDateMetricParams
        @StatsDateMetricParams(name = "Long-1")
        long long1;

        @StatsDateMetricParams
        Long longObj1;

        @StatsDateMetricParams
        int int1;

        @StatsDateMetricParams
        Integer intObj1;

        @StatsDateMetricParams
        double double1;

        @StatsDateMetricParams
        Double doubleObj1;

        @StatsDateMetricParams
        Long longNullObj1;

        @StatsDateMetricParams
        FlexLong flexLong1;

        @StatsDateMetricParams
        FlexDouble flexDouble1;

        @StatsDateMetricParams
        AtomicLong atomicLong1;

        @StatsDateMetricParams
        AtomicInteger atomicInteger1;

    }


    // Check date metric field
    @Test()
    public void testDateMetrics() {
        StatsService        statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();
        StatsTestingEngine statsEngine  = (StatsTestingEngine)statsService.getStatsEngine();

        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.setManualUpdateMode(true);

        DateMetrics metrics = new DateMetrics(statsService, attributes);


        // --- step 1 ---

        metrics.long1      =      LocalDateTime.of(2018,11,25,23,59,30,0).toEpochSecond(ZoneOffset.UTC) * 1000L; // mSec
        metrics.longObj1   =      LocalDateTime.of(2018,11,25,23,59,31,0).toEpochSecond(ZoneOffset.UTC) * 1000000000L; // nSec
        metrics.int1       = (int)LocalDateTime.of(2018,11,25,23,59,32,0).toEpochSecond(ZoneOffset.UTC);
        metrics.intObj1    = (int)LocalDateTime.of(2018,11,25,23,59,33,0).toEpochSecond(ZoneOffset.UTC);

        metrics.double1    =         LocalDateTime.of(2018,11,25,23,59,41,0).toEpochSecond(ZoneOffset.UTC);
        metrics.doubleObj1 = (double)LocalDateTime.of(2018,11,25,23,59,42,0).toEpochSecond(ZoneOffset.UTC);


        metrics.longNullObj1   = null;

        metrics.flexLong1      = new FlexLong(          LocalDateTime.of(2018,11,25,23,59,51,0).toEpochSecond(ZoneOffset.UTC));
        metrics.flexDouble1    = new FlexDouble((double)LocalDateTime.of(2018,11,25,23,59,52,0).toEpochSecond(ZoneOffset.UTC));

        metrics.atomicLong1    = new AtomicLong(        LocalDateTime.of(2018,11,25,23,59,21,0).toEpochSecond(ZoneOffset.UTC) * 1000000); // uSec
        metrics.atomicInteger1 = new AtomicInteger((int)LocalDateTime.of(2018,11,25,23,59,22,0).toEpochSecond(ZoneOffset.UTC));

        final long measurementEpoch1 = 10000;
        metrics.manualUpdate(measurementEpoch1);

        // Get the data
        StatsEngineMetricsGroupData metricsData = statsEngine.getLatestMetricsGroupData("DATE-METRICS");
        Assert.assertNotNull(metricsData);

        // Check metrics
        Assert.assertEquals(measurementEpoch1, metricsData.getMeasurementEpoch());

        Assert.assertEquals((Long) 20181125235930L, engineGroupDataGetLongValueByName(metricsData, "long1"));
        Assert.assertEquals((Long) 20181125235930L, engineGroupDataGetLongValueByName(metricsData, "Long-1"));
        Assert.assertEquals((Long) 20181125235931L, engineGroupDataGetLongValueByName(metricsData, "longObj1"));
        Assert.assertEquals((Long) 20181125235932L, engineGroupDataGetLongValueByName(metricsData, "int1"));
        Assert.assertEquals((Long) 20181125235933L, engineGroupDataGetLongValueByName(metricsData, "intObj1"));

        Assert.assertEquals((Long) 20181125235941L, engineGroupDataGetLongValueByName(metricsData, "double1"));
        Assert.assertEquals((Long) 20181125235942L, engineGroupDataGetLongValueByName(metricsData, "doubleObj1"));

        Assert.assertNull(engineGroupDataGetLongValueByName(metricsData, "longNullObj1"));

        Assert.assertEquals((Long) 20181125235951L, engineGroupDataGetLongValueByName(metricsData, "flexLong1"));
        Assert.assertEquals((Long) 20181125235952L, engineGroupDataGetLongValueByName(metricsData, "flexDouble1"));

        Assert.assertEquals((Long) 20181125235921L, engineGroupDataGetLongValueByName(metricsData, "atomicLong1"));
        Assert.assertEquals((Long) 20181125235922L, engineGroupDataGetLongValueByName(metricsData, "atomicInteger1"));

    }


}
