package fortscale.monitoring.external.stats.collector.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fortscale.monitoring.external.stats.collector.ExternalStatsCollectorService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * An abstract based class for external collector service. Its main function is to create a periodic tick and call the
 * collect() function to do the read work
 *
 * Created by gaashh on 6/5/16.
 */
abstract public class AbstractExternalStatsCollectorServiceImpl implements ExternalStatsCollectorService {

    private static final Logger logger = Logger.getLogger(AbstractExternalStatsCollectorServiceImpl.class);


    // Collector service name. Used mainly for logging
    protected String collectorServiceName;

    // Out stats service
    protected StatsService statsService;

    // Enable tick thread
    boolean isTickThreadEnabled;

    // Tick thread period
    long tickPeriodSeconds;

    // Tick period warning threshold
    long tickSlipWarnSeconds;

    // Next expected tick epoch
    long expectedTickEpoch;

    // self metrics
    protected ExternalStatsCollectorMetrics selfMetrics;

    /**
     *
     * ctor
     *
     * @param collectorServiceName - Collector service name. Used mainly for logging
     * @param statsService         - The stats service. might be null
     * @param isTickThreadEnabled  - Enable tick thread. Typically true
     * @param tickPeriodSeconds    - Tick thread period
     * @param tickSlipWarnSeconds  - ick period warning threshold
     */
    public AbstractExternalStatsCollectorServiceImpl(String collectorServiceName, StatsService statsService,
                                                     boolean isTickThreadEnabled,
                                                     long tickPeriodSeconds, long tickSlipWarnSeconds){
        this.collectorServiceName = collectorServiceName;
        this.statsService         = statsService;
        this.isTickThreadEnabled  = isTickThreadEnabled;
        this.tickPeriodSeconds    = tickPeriodSeconds;
        this.tickSlipWarnSeconds  = tickSlipWarnSeconds;

        selfMetrics = new ExternalStatsCollectorMetrics(statsService,collectorServiceName);

        logger.info("Creating stats collector service {}. statsService={} isTickThreadEnabled={} tickPeriodSeconds={} tickSlipWarnSeconds={}",
                collectorServiceName, statsService, isTickThreadEnabled,  tickPeriodSeconds, tickSlipWarnSeconds);
    }

    /**
     *
     * collect() is implemented by the derived class to do the actual collection.
     * Typically, it is called from tick(). It might be called directly for testing
     *
     * @param epoch - the measurement time
     */
    abstract public void collect(long epoch);

    /**
     * Called by the derived class to start the collection. It must be called as the last step of the derived class ctor
     * to ensure ctor completed before starting to collect via a periodic thread
     *
     * start() creates a periodic tick thread unless disabled
     *
     */
    protected void start() {

        logger.debug("Collector service {} started", collectorServiceName);
        // Create the thread unless disabled
        if (isTickThreadEnabled) {

            // Create the tick thread object
            AbstractExternalStatsCollectorServiceImpl me = this;
            Runnable task = () -> {
                    long epoch = System.currentTimeMillis() / 1000;
                    me.tick(epoch);
                };

            // Create the periodic tick thread
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat( String.format("collector[%s]-tick[%%s]", collectorServiceName))
                            .build());
            int initialDelay = 0;
            executor.scheduleAtFixedRate(task, initialDelay, this.tickPeriodSeconds, TimeUnit.SECONDS);
        }
        else {
            logger.info("Collector {} tick task is disabled", collectorServiceName);
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
                logger.debug("Collector {} tick occurred too soon. Threshold hold is {} seconds",
                        collectorServiceName, epoch - expectedTickEpoch, tickSlipWarnSeconds);
                selfMetrics.collectionsTooEarly++;
                return;
            }

            // If slipped for too long, issue a warning
            if (epoch > expectedTickEpoch + tickSlipWarnSeconds) {
                logger.warn("Collector {} tick slipped for too long, {} seconds. Threshold hold is {} seconds",
                        collectorServiceName, epoch - expectedTickEpoch, tickSlipWarnSeconds);
                selfMetrics.collectionsDelayed++;
            }

            logger.debug("Collector {} tick called at {}", collectorServiceName, epoch);

            logger.debug("Collector {} tick started. period={} delta={} epoch={} expectedEpoch={}",
                    collectorServiceName, tickPeriodSeconds, epoch - expectedTickEpoch, epoch, expectedTickEpoch);

            // Update the expected time
            expectedTickEpoch += tickPeriodSeconds;

            selfMetrics.collects++;
            // Do some real work :-)
            collect(epoch);

            logger.debug("stats service metrics update tick completed");

        }
        catch (Exception ex) {
            selfMetrics.collectFailures++;
            String msg = String.format("Ignoring unexpected exception collector %s at tick function at %s",
                    collectorServiceName, epoch);
            logger.error(msg, ex);
        }
    }

}
