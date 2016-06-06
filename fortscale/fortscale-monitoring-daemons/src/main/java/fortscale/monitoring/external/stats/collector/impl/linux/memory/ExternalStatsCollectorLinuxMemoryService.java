package fortscale.monitoring.external.stats.collector.impl.linux.memory;

import fortscale.monitoring.external.stats.collector.impl.AbstractExternalStatsCollectorServiceImpl;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by gaashh on 6/5/16.
 */
public class ExternalStatsCollectorLinuxMemoryService extends AbstractExternalStatsCollectorServiceImpl {

    private static final Logger logger = Logger.getLogger(ExternalStatsCollectorLinuxMemoryService.class);


    final static String COLLECTOR_SERVICE_NAME = "linuxMemory";

    protected ExternalStatsCollectorLinuxMemoryCollector collector;

    public ExternalStatsCollectorLinuxMemoryService(StatsService statsService, String procBasePath,
                                                    long tickPeriodSeconds, long tickSlipWarnSeconds) {
        super(COLLECTOR_SERVICE_NAME, statsService, tickPeriodSeconds, tickSlipWarnSeconds);

        collector = new ExternalStatsCollectorLinuxMemoryCollector(collectorServiceName, this.statsService, procBasePath);

        // Start doing the real work
        start();

    }
    public void collect(long epoch) {

        collector.collect(epoch);
    }

}
