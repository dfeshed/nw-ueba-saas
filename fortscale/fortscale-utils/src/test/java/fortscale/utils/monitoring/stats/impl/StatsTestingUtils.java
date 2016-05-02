package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.engine.StatsEngine;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;

/**
 *
 * A class that hosts various testing utils.
 *
 * It is abstract class to make sure all its functions are static.
 *
 * Created by gaashh on 4/27/16.
 */
abstract public class StatsTestingUtils {


    // An helper function that creates a stats service with testing engine
    static public StatsServiceImpl createStatsServiceImplWithTestingEngine(){

        // Create the testing engine
        StatsEngine statsEngine  = new StatsTestingEngine();

        // Create the stats service and hook the engine to it
        StatsServiceImpl statsService = new StatsServiceImpl(statsEngine);

        return statsService;
    }
}
