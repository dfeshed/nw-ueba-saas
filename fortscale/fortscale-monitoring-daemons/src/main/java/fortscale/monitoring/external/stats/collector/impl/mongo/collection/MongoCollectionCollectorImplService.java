package fortscale.monitoring.external.stats.collector.impl.mongo.collection;

import fortscale.monitoring.external.stats.collector.impl.AbstractExternalStatsCollectorServiceImpl;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * A service that collects mongo collection information from collection.stats() files and writes them to stats metrics group
 */
public class MongoCollectionCollectorImplService extends AbstractExternalStatsCollectorServiceImpl {

    // Collector service name. Used for logging
    final static String COLLECTOR_SERVICE_NAME = "mongoCollection";
    private MongoCollectionCollectorImpl collector;
    private MongoTemplate mongoTemplate;

    /**
     * ctor
     *
     * @param statsService         - The stats service. might be null
     * @param isTickThreadEnabled  - Enable tick thread. Typically true
     * @param tickPeriodSeconds    - Tick thread period
     * @param tickSlipWarnSeconds  - ick period warning threshold
     */
    public MongoCollectionCollectorImplService(StatsService statsService, boolean isTickThreadEnabled, long tickPeriodSeconds, long tickSlipWarnSeconds, MongoTemplate mongoTemplate) {
        super(COLLECTOR_SERVICE_NAME, statsService, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);
        this.mongoTemplate=mongoTemplate;

        // initiate collector
        collector = new MongoCollectionCollectorImpl(this.mongoTemplate,this.statsService,selfMetrics);

        // Start doing the real work
        start();
    }


    /**
     * collect the data by calling our collector.
     *
     * This function is typically called from the parent class at the tick
     *
     *
     * @param epoch - the measurement time
     */
    @Override
    public void collect(long epoch) {
        collector.collect(epoch);
    }
}
