package fortscale.monitoring.external.stats.collector.impl;

import fortscale.monitoring.external.stats.collector.ExternalStatsCollectorService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.impl.StatsServiceTick;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gaashh on 6/5/16.
 */
abstract public class AbstractExternalStatsCollectorServiceImpl implements ExternalStatsCollectorService {

    private static final Logger logger = Logger.getLogger(AbstractExternalStatsCollectorServiceImpl.class);

    protected StatsService statsService;
    protected String collectorServiceName;

    // Tick period
    long tickPeriodSeconds;

    // Tick period warning threshold
    long tickSlipWarnSeconds;

    // Next expected tick epoch
    long expectedTickEpoch;

    public AbstractExternalStatsCollectorServiceImpl(String collectorServiceName, StatsService statsService,
                                                     long tickPeriodSeconds, long tickSlipWarnSeconds){
        this.collectorServiceName = collectorServiceName;
        this.statsService         = statsService;
        this.tickPeriodSeconds    = tickPeriodSeconds;
        this.tickSlipWarnSeconds  = tickSlipWarnSeconds;
    }

    abstract public void collect(long epoch);

    protected void start() {

        AbstractExternalStatsCollectorServiceImpl me = this;

        // Create the thread unless disabled
        if (tickPeriodSeconds > 0) {

            // Create the tick thread object
            Runnable task = () -> {
                    long epoch = System.currentTimeMillis() / 1000;
                    me.tick(epoch);
                };

            // Create the periodic tick thread
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            int initialDelay = 0;
            executor.scheduleAtFixedRate(task, initialDelay, this.tickPeriodSeconds, TimeUnit.SECONDS);
        }
        else {
            logger.info("Stats tick task disabled");
        }


    }

    /**
     *
     * Called periodically from tick thread. It does the following:
     *
     *   1. Check if function called to early. If so, do nothing
     *   2. Check if function called too late (slip). If so, issue a warning (and move on)
     *   3. Call derived class collect() to do the real work
     *
     * @param epoch - time when tick occurred. This epoch as parameter enables easy testing
     */

    public void tick(long epoch) {

        try {

            // If the first time, update the expected epoch
            if (expectedTickEpoch == 0) {
                expectedTickEpoch = epoch;
            }

            // If too early, do nothing
            if (epoch < expectedTickEpoch) {
                return;
            }

            // If slipped for too long, issue a warning
            if (epoch > expectedTickEpoch + tickSlipWarnSeconds) {
                logger.warn("Collector {} tick slipped for too long, {} seconds. Threshold hold is {} seconds",
                        collectorServiceName, epoch - expectedTickEpoch, tickSlipWarnSeconds);
            }

            logger.debug("Collector {} tick called at {}", collectorServiceName, epoch);

            logger.debug("Collector {} tick started. period={} delta={} epoch={} expectedEpoch={}",
                    collectorServiceName, tickPeriodSeconds, epoch - expectedTickEpoch, epoch, expectedTickEpoch);

            // Update the expected time
            expectedTickEpoch += tickPeriodSeconds;

            // Do some real work :-)
            collect(epoch);

            logger.debug("stats service metrics update tick completed");

        }
        catch (Exception ex) {
            String msg = String.format("Ignoring unexpected exception collector %s at tick function at %s",
                    collectorServiceName, epoch);
            logger.error(msg, ex);
        }

    }


}
