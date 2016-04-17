package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsMetricsGroupHandler;
import fortscale.services.monitoring.stats.StatsService;
import fortscale.services.monitoring.stats.engine.StatsEngine;
import fortscale.utils.logging.Logger;

import java.util.LinkedList;
import java.util.List;

// TODO: add @Service("sshUsersWhitelist") for spring


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

    // The service stats engine. It must be set before any metricsGroup is registered.
    StatsEngine statsEngine;

    // A list of registered metrics group handlers. Note the metrics group handler holds the metrics group.
    List<StatsMetricsGroupHandler> metricsGroupHandlersList;

    /**
     * ctor
     */
    public StatsServiceImpl() {

        logger.info("Creating StatsServiceImpl instance");

        metricsGroupHandlersList = new LinkedList<>();

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
    // TODO: multithreading !!!
    public StatsMetricsGroupHandler registerStatsMetricsGroup(StatsMetricsGroup metricsGroup) {

        logger.debug("Registering StatsMetricsGroup class {} instrumented class{}",
                      metricsGroup.getClass().getName(), metricsGroup.getInstrumentedClass().getName());

        // Verify stats engine is registered
        if (statsEngine == null) {
            logger.error("Registering metrics group {} without stats engine set", metricsGroup.getClass().getName());
            String msg = String.format("Registering metrics group %s without stats engine set",
                                        metricsGroup.getClass().getName());
            throw (new StatsMetricsExceptions.NoStatsEngineException(msg));
        }

        try { // Just in case

            // Creates an handler for the statsGroup and bind it to this service
            StatsMetricsGroupHandlerImpl groupHandler = new StatsMetricsGroupHandlerImpl(metricsGroup, this);

            //  Add the group handler to the group handler list
            metricsGroupHandlersList.add(groupHandler);

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
     * Register a StatsEngine to the service.
     *
     * The engine is registered once and must be set before any metrics is registered
     *
     * @param statsEngine - the engine to register
     */
    public void registerStatsEngine(StatsEngine statsEngine) {

        // Verify stats engine is not already registered
        if (this.statsEngine != null) {

            logger.error("Registering stats engine {} while {} engine is already registered",
                      statsEngine.getClass().getName(), this.statsEngine.getClass().getName() );

            String msg = String.format("Registering stats engine %s while %s engine is already registered",
                                       statsEngine.getClass().getName(), this.statsEngine.getClass().getName() );

            throw (new StatsMetricsExceptions.StatsEngineAlreadyRegisteredException(msg));

        }

        // Register the engine
        this.statsEngine = statsEngine;

        logger.debug("Registered stats engine {}", statsEngine.getClass().getName());
    }

    /**
     * Collect all the registered application metrics and writes them to the engine by calling all the metrics groups
     * handlers
     *
     * This is an internal method and should not be called by the application (except for testing)
     *
     * @param epochTime metrics epoch time. Zero indicates now
     */
    // TODO: multithreading !!!
    public void writeMetricsGroupsToEngine(long epochTime){

        logger.debug("Writing metrics groups to engine. EpochTime is {}", epochTime);

        try { // Just in case

            // Loop all the metrics groups handlers and ask them to collect and write their metrics
            for (StatsMetricsGroupHandler metricsGroupHandler : metricsGroupHandlersList) {
                metricsGroupHandler.writeMetricGroupsToEngine(epochTime);
            }

        }

        catch (Exception ex) {
            logger.error("Unexpected error while writing metrics groups to engine", ex);
        }
    }

    // --- getters / setters

    public StatsEngine getStatsEngine() {
        return statsEngine;
    }

}
