package fortscale.utils.monitoring.stats.service;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

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
    StatsServiceTestingTrigoServiceMetrics(StatsService statsService, String typeTag, String speedTag) {

        // Call parent ctor
        super(statsService, StatsTopicServicePeriodicTest.class,
              // Create anonymous attribute class with initializer block since it does not have ctor
              new StatsMetricsGroupAttributes() {
                  {
                     addTag("type",  typeTag);
                     addTag("speed", speedTag);
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

}


public class StatsServiceTestingTrigoService {

    long dgreeRate;
    long degree = 0;
    StatsServiceTestingTrigoServiceMetrics metrics;

    StatsServiceTestingTrigoService(StatsService statsService, String type, String speed, long degreeRate) {

        this.dgreeRate = degreeRate;

        // Init metric
        metrics = new StatsServiceTestingTrigoServiceMetrics(statsService, type, speed);
    }

    void doIt() {

        degree += dgreeRate;

        metrics.doItCount++;
        metrics.degree = degree;
        metrics.cosine = Math.cos(Math.toRadians((double)degree));
        metrics.sine   = Math.sin(Math.toRadians((double)degree));

    }

    void manualUpdate(long epoch) {
        metrics.manualUpdate(epoch);
    }

}

