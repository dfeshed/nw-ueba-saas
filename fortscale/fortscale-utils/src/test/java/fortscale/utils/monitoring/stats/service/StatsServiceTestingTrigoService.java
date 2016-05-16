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
class StatsServiceTestingTrigoServiceMetric extends StatsMetricsGroup {

    StatsServiceTestingTrigoServiceMetric(StatsService statsService, StatsMetricsGroupAttributes attributes) {
        super(statsService, StatsTopicServicePeriodicTest.class, attributes);
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
    StatsServiceTestingTrigoServiceMetric metrics;

    StatsServiceTestingTrigoService(StatsService statsService, String type, String speed, long degreeRate) {

        this.dgreeRate = degreeRate;

        // Init metric
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();

        attributes.addTag("type",  type);
        attributes.addTag("speed", speed);

        metrics = new StatsServiceTestingTrigoServiceMetric(statsService, attributes);
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

