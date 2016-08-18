package fortscale.utils.monitoring.stats.service;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by gaashh on 5/4/16.
 */

@StatsMetricsGroupParams(name = "testMetricsTrigo")
class StatsServiceTestingTrigoServiceMetrics extends StatsMetricsGroup {

    /**
     *
     * ctor
     *
     * @param statsService
     * @param typeTag
     * @param speedTag
     */
    StatsServiceTestingTrigoServiceMetrics(StatsService statsService, String typeTag, String speedTag, boolean isManualUpdateMode) {

        // Call parent ctor
        super(statsService, StatsTopicServicePeriodicTest.class,
              // Create anonymous attribute class with initializer block since it does not have ctor
              new StatsMetricsGroupAttributes() {
                  {
                     addTag("type",  typeTag);
                     addTag("speed", speedTag);
                     setManualUpdateMode(isManualUpdateMode);
                  }
              } );
    }

    @StatsLongMetricParams
    public long doItCount;

    @StatsLongMetricParams
    public long degree;

    @StatsDoubleMetricParams
    public double sine;

    @StatsDoubleMetricParams
    public double cosine;

    @StatsDateMetricParams(name="fakeDate")
    public long fakeEpoch;

    @StatsStringMetricParams // DO NOT USE STRINGS UNLESS YOU HAVE TO
    public String sineSignText;

}


public class StatsServiceTestingTrigoService {

    long degreeRate;
    long degree = 0;
    StatsServiceTestingTrigoServiceMetrics metrics;

    StatsServiceTestingTrigoService(StatsService statsService, String type, String speed, long degreeRate, boolean isManualUpdateMode) {

        this.degreeRate = degreeRate;

        // Init metric
        metrics = new StatsServiceTestingTrigoServiceMetrics(statsService, type, speed, isManualUpdateMode);
    }

    void doIt() {

        degree += degreeRate;

        metrics.doItCount++;
        metrics.degree    = degree;
        metrics.cosine    = Math.cos(Math.toRadians((double)degree));
        metrics.sine      = Math.sin(Math.toRadians((double)degree));
        metrics.fakeEpoch = LocalDateTime.of(2022,1,1,0,0,0,0).toEpochSecond(ZoneOffset.UTC) + degree * 3600;
        if (metrics.sine > 0) {
            metrics.sineSignText = "positive";
        }
        else {
            metrics.sineSignText = "negative or zero";
        }

    }

    void manualUpdate(long epoch) {
        metrics.manualUpdate(epoch);
    }

}

