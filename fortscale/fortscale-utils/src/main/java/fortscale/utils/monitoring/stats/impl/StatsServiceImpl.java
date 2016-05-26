package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroupHandler;
import fortscale.utils.monitoring.stats.engine.StatsEngine;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.logging.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * StatsService main implementation. See StatService for common documentation.
 *
 * It has 2 main functions:
 *   1. Register the metrics groups
 *   2. Sample the metrics groups and writes the metrics values to the engine
 *
 * There shall be one instance of this class per process.
 *
 * Created by gaashh on 4/3/16.
 */

public class StatsServiceImpl implements StatsService {

    private static final Logger logger = Logger.getLogger(StatsServiceImpl.class);

    // The stats engine instance.
    StatsEngine statsEngine = null;

    // A list of registered metrics group handlers. Note the metrics group handler holds the metrics group.
    // The list is accessed from multiple threads: a few thread the register metrics, threads that scan the metrics for updates
    // The list is protected by metricsGroupHandlersListLock
    List<StatsMetricsGroupHandlerImpl> metricsGroupHandlersList = new LinkedList<>();

    // See metricsGroupHandlersList
    Object metricsGroupHandlersListLock = new Object();

    // Tick thread period. Zero -> disable
    final long tickSeconds;

    // Periodic metrics update period in seconds. Zero -> disable
    final long metricsUpdatePeriodSeconds;

    // Periodic metrics update - issue warning message if the actual time is greater then the expected time plus the slip warning gap
    final long metricsUpdateSlipWarnSeconds;

    // Periodic metrics update - the time when the next update is expected
    long expectedMetricsUpdateEpoch = 0;

    // Engine push period in seconds. Zero -> disable
    final long enginePushPeriodSeconds;

    // Engine push - issue warning message if the actual time is greater then the expected time plus the slip warning gap
    final long enginePushSlipWarnSeconds;

    // Engine push - the time when the next push is expected
    long expectedEnginePushEpoch = 0;

    // see ctor doc
    boolean isExternalMetricUpdateTick;

    // see ctor doc
    boolean isExternalEnginePushTick;

    /**
     * ctor - creates the stats service and creates the tick thread
     *
     * @param statsEngine                   - the stats engine to work with
     * @param tickSeconds                   - Tick thread period. Zero -> disable
     * @param metricsUpdatePeriodSeconds    - Periodic metrics update period in seconds. Zero -> disable
     * @param metricsUpdateSlipWarnSeconds  - Periodic metrics update - issue warning message if the actual time is
     *                                        greater then the expected time plus the slip warning gap
     * @param enginePushPeriodSeconds       - Engine push period in seconds. Zero -> disable
     * @param enginePushSlipWarnSeconds     - Engine push - issue warning message if the actual time is
     *                                        greater then the expected time plus the slip warning gap
     * @param isExternalMetricUpdateTick    - False - metrics are updated by the tick internal thread (the typical case)
     *                                        True -> external thread will update the metrics by calling
     *                                        externalMetricUpdateTick().
     *
     * @param isExternalEnginePushTick      - False - engine is pushed by the tick internal thread (the typical case)
     *                                        True -> external thread will push the engine by calling tbd()
     */
    public StatsServiceImpl(StatsEngine statsEngine, long tickSeconds,
                            long metricsUpdatePeriodSeconds,    long metricsUpdateSlipWarnSeconds,
                            long enginePushPeriodSeconds,       long enginePushSlipWarnSeconds,
                            boolean isExternalMetricUpdateTick, boolean isExternalEnginePushTick) {

        logger.info("Creating StatsServiceImpl instance with engine={} tickSeconds={}" +
                    "metricsUpdatePeriodSeconds={} metricsUpdateSlipWarnSeconds={}" +
                    "enginePushPeriodSeconds={} enginePushSlipWarnSeconds={}" +
                    "isExternalMetricUpdateTick={} isExternalEnginePushTick={}",
                    statsEngine.getClass().getName(), tickSeconds,
                    metricsUpdatePeriodSeconds, metricsUpdateSlipWarnSeconds,
                    enginePushPeriodSeconds,    enginePushSlipWarnSeconds,
                    isExternalMetricUpdateTick, isExternalEnginePushTick);

        // Save vars
        this.statsEngine = statsEngine;
        this.tickSeconds = tickSeconds;

        this.metricsUpdatePeriodSeconds   = metricsUpdatePeriodSeconds;
        this.metricsUpdateSlipWarnSeconds = metricsUpdateSlipWarnSeconds;

        this.enginePushPeriodSeconds      = enginePushPeriodSeconds;
        this.enginePushSlipWarnSeconds    = enginePushSlipWarnSeconds;

        this.isExternalMetricUpdateTick   = isExternalMetricUpdateTick;
        this.isExternalEnginePushTick     = isExternalEnginePushTick;

        // Create tick thread if enabled
        if (tickSeconds > 0) {

            // Crate the tick thread object
            StatsServiceTick task = new StatsServiceTick(this);

            // Create the periodic tick thread
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            int initialDelay = 0;
            executor.scheduleAtFixedRate(task, initialDelay, this.tickSeconds, TimeUnit.SECONDS);
        }
        else {
            logger.info("Stats tick task disabled");
        }

    }

    /**
     *
     * Testing only ctor. It does not init the tick thread and setups for external ticks
     *
     * @param statsEngine - the stats engine to work with
     */
    public StatsServiceImpl(StatsEngine statsEngine) {
        this(statsEngine, 0, 0, 0, 0, 0, true, true);
    }

    /**
     *  Register StatsMetricsGroup object to the stats service.
     *
     *  The registration has those steps:
     *    1. Verify a stats engine is registered
     *    2. Creates an group handler for the statsGroup and bind it to this service
     *    3. Add the group handler to the service metrics group handler list
     *
     * @param metricsGroup - the metrics group to register
     * @return - metrics group handler (it is saved in the metrics group for back reference)
     */
    public StatsMetricsGroupHandler registerStatsMetricsGroup(StatsMetricsGroup metricsGroup) {

        logger.debug("Registering StatsMetricsGroup class {} instrumented class{}",
                      metricsGroup.getClass().getName(), metricsGroup.getInstrumentedClass().getName());

        try { // Just in case

            // Creates an handler for the statsGroup and bind it to this service
            StatsMetricsGroupHandlerImpl groupHandler = new StatsMetricsGroupHandlerImpl(metricsGroup, this);

            // Add the group handler to the group handler list
            // Be thread safe :-)
            synchronized (metricsGroupHandlersListLock) {
                metricsGroupHandlersList.add(groupHandler);
            }

            return groupHandler;

        }
        catch (Exception ex){
            logger.error("A problem while registering metrics group {} had obscured. Instrumented class is {}",
                      metricsGroup.getClass().getName(), metricsGroup.getInstrumentedClass().getName() );

            String msg = String.format("A problem while registering metrics group %s had obscured. Instrumented class is %s",
                                        metricsGroup.getClass().getName(), metricsGroup.getInstrumentedClass().getName() );
            throw (new StatsMetricsExceptions.ProblemWhileRegisteringMetricsGroupException(msg, ex));

        }

    }


    /**
     * Collect the registered application metrics and writes them to the engine by calling all the metrics groups
     * handlers. Note only metrics groups that are not marked as manual update mode are considered
     *
     * This is an internal method and should not be called by the application (except for testing)
     *
     * @param epochTime metrics epoch time. Zero indicates now
     */
    public void writeMetricsGroupsToEngine(long epochTime){

        logger.debug("Writing metrics groups to engine. EpochTime is {}", epochTime);

        try { // Just in case

            // Loop all the metrics groups handlers and ask them to collect and write their metrics
            // Skip manual update mode metrics groups
            // Be thread safe
            synchronized (metricsGroupHandlersListLock) {
                for (StatsMetricsGroupHandlerImpl metricsGroupHandler : metricsGroupHandlersList) {

                    // Check manual update mode
                    boolean isManualUpdateMode = metricsGroupHandler.isManualUpdateMode();
                    if ( isManualUpdateMode ) {
                        // Manual update mode, log it and skip
                        logger.debug("Writing metrics groups to engine, skipping manual update mode metrics group" +
                                     "metricsGroup={} instrumentedClass={} attributes={} epochTime={}",
                                     metricsGroupHandler.getGroupName(),
                                     metricsGroupHandler.getMetricsGroupInstrumentedClass().getName(),
                                     metricsGroupHandler.getMetricsGroupAttributes().toStringShort(),
                                     epochTime  );
                    }
                    else {
                        // Not manual update model, do it
                        metricsGroupHandler.writeMetricGroupsToEngine(epochTime);
                    }
                }
            }

        }

        catch (Exception ex) {
            logger.error("Unexpected error while writing metrics groups to engine", ex);
        }
    }

    /**
     * See interface documentation
     */
    public void externalMetricsUpdateTick(long epochTime) {

        try {
            // Check we are not in external mode, if do long an error and return
            if (!isExternalMetricUpdateTick) {
                logger.error("externalMetricsUpdateTick() but not in external metric update mode. Call ignored");
                return;
            }

            // If epoch is zero, use current time
            if (epochTime == 0) {
                epochTime = System.currentTimeMillis() / 1000;
            }

            // Do it
            logger.debug("external metrics update tick called at {}", epochTime);
            tickMetricsUpdate(epochTime);
        }
        catch (Exception ex) {
            logger.error("Ignoring unexpected exception at external metrics update tick function", ex);
        }

    }



    /**
     * See interface documentation
     */
    public void manualUpdatePush() {

        logger.debug("manualUpdatePush() called");

        // Make sure no exceptions are thrown
        try {
            // Call the engine to do the real work
            getStatsEngine().flushMetricsGroupData();
        }
        catch (Exception ex) {
            logger.error("Got an exception while pushing data to the engine", ex);
        }

    }

    /**
     *
     * Called periodically from tick thread. If enabled it will:
     * 1. Update the metrics groups
     * 2. Flush the engine data to its destination
     *
     * @param epoch - time when tick occurred. This epoch as parameter enables easy testing
     */
    public void tick(long epoch) {

        try {

            logger.trace("StatsService tick called at {}", epoch);

            // Order is important, keep it!

            // Update metrics, if enabled
            if ( !isExternalMetricUpdateTick && metricsUpdatePeriodSeconds > 0) {
                tickMetricsUpdate(epoch);
            }

            // Engine push, if enabled
            if ( !isExternalEnginePushTick && enginePushPeriodSeconds > 0) {
                tickEnginePush(epoch);
            }


        }
        catch (Exception ex) {
            logger.error("Ignoring unexpected exception at stats service tick function", ex);
        }

    }

    /**
     *
     * Called from tick() to update metrics (if enabled)
     *
     * It does the following:
     * 1. Check if function called to early. If so, do nothing
     * 2. Check if function called too late (slip). If so, issue a warning (and move on)
     * 3. Call writeMetricsGroupsToEngine() to do the real work, metric update.
     *    Note updates only metrics groups that are not set for manual update mode
     *
     * @param epoch
     */
    protected void tickMetricsUpdate(long epoch) {

        // If the first time, update the expected epoch
        if (expectedMetricsUpdateEpoch == 0) {
            expectedMetricsUpdateEpoch = epoch;
        }

        // If too early, do nothing
        if (epoch < expectedMetricsUpdateEpoch) {
            return;
        }

        // If slipped for too long, issue a warning
        if (epoch > expectedMetricsUpdateEpoch + metricsUpdateSlipWarnSeconds) {
            logger.warn("Metric update tick slipped for too long {} seconds. Threshold hold is {} seconds",
                        epoch - expectedMetricsUpdateEpoch,
                        metricsUpdateSlipWarnSeconds);
        }

        logger.debug("stats service metrics update tick started. period={} delta={} epoch={} expectedEpoch={} " +
                     "isExternalMetricUpdateTick={}",
                     metricsUpdatePeriodSeconds, epoch - expectedMetricsUpdateEpoch, epoch, expectedMetricsUpdateEpoch,
                     isExternalMetricUpdateTick);

        // Update the expected time
        expectedMetricsUpdateEpoch += metricsUpdatePeriodSeconds;

        // Do some real work :-)
        writeMetricsGroupsToEngine(epoch);

        logger.debug("stats service metrics update tick completed");

    }


    /**
     *
     * Called from tick() to push metric from engine to destination (if enabled)
     *
     * It does the following:
     * 1. Check if function called to early. If so, do nothing
     * 2. Check if function called too late (slip). If so, issue a warning (and move on)
     * 3. Call the engine flushMetricsGroupData() to do the real work
     *
     * @param epoch
     */
    protected void tickEnginePush(long epoch) {

        // If the first time, update the expected epoch
        if (expectedEnginePushEpoch == 0) {
            expectedEnginePushEpoch = epoch;
        }

        // If too early, do nothing
        if (epoch < expectedEnginePushEpoch) {
            return;
        }

        // If slipped for too long, issue a warning
        if (epoch > expectedEnginePushEpoch + enginePushSlipWarnSeconds) {
            logger.warn("Engine push  tick slipped for too long {} seconds. Threshold hold is {} seconds",
                    epoch - expectedEnginePushEpoch,
                    enginePushSlipWarnSeconds);
        }

        logger.debug("stats service engine push tick started. period={} delta={} epoch={} expectedEpoch={}",
                enginePushPeriodSeconds, epoch - expectedEnginePushEpoch, epoch, expectedEnginePushEpoch);

        // Update the expected time
        expectedEnginePushEpoch += enginePushPeriodSeconds;

        // Do some real work :-) and make sure no exceptions are thrown
        try {
            // Call the engine to do the real work
            getStatsEngine().flushMetricsGroupData();
        }
        catch (Exception ex) {
            logger.error("Got an exception while pushing data to the engine", ex);
        }

        logger.debug("stats service engine push tick completed");

    }


    // --- getters / setters

    public StatsEngine getStatsEngine() {
        return statsEngine;
    }

}
