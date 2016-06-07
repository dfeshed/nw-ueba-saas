package fortscale.monitoring.external.stats.collector.impl.mongo.server;

import fortscale.monitoring.external.stats.collector.impl.AbstractExternalStatsCollectorServiceImpl;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * A service that collects mongo DB information from db.serverStatus() files and writes them to stats metrics group
 */
public class MongoServerCollectorImplService extends AbstractExternalStatsCollectorServiceImpl {

    // Collector service name. Used for logging
    final static String COLLECTOR_SERVICE_NAME = "mongoServer";
    private MongoServerCollectorImpl collector;
    private MongoTemplate mongoTemplate;

    /**
     * ctor
     *
     * @param statsService         - The stats service. might be null
     * @param isTickThreadEnabled  - Enable tick thread. Typically true
     * @param tickPeriodSeconds    - Tick thread period
     * @param tickSlipWarnSeconds  - ick period warning threshold
     */
    public MongoServerCollectorImplService(StatsService statsService, boolean isTickThreadEnabled, long tickPeriodSeconds, long tickSlipWarnSeconds, MongoTemplate mongoTemplate) {
        super(COLLECTOR_SERVICE_NAME, statsService, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);
        this.mongoTemplate=mongoTemplate;

        // initiate collector
        collector = new MongoServerCollectorImpl(this.mongoTemplate,this.statsService);

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
