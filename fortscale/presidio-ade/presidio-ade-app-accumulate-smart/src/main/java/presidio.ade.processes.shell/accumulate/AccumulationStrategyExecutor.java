package presidio.ade.processes.shell.accumulate;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;

import java.util.List;

/**
 * Brakes timeRange to partitions by strategy.
 * Execute accumulation of SingleTimeRange.
 */
public abstract class AccumulationStrategyExecutor {
    private static final Logger logger = Logger.getLogger(AccumulationStrategyExecutor.class);

    protected final FixedDurationStrategy strategy;

    public AccumulationStrategyExecutor(FixedDurationStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * brakes given time range to smaller partitions by {@link this#strategy} and execute upon them
     *
     * @param timeRange start and end time of data to be executed upon
     */
    public void execute(TimeRange timeRange, String configurationName) {
        logger.debug(String.format("got execution time range=%s", timeRange));
        List<TimeRange> partitionedTimeRanges = FixedDurationStrategyUtils.splitTimeRangeByStrategy(timeRange, strategy);

        for (TimeRange timePartition : partitionedTimeRanges) {
            logger.debug(String.format("executing on time partition=%s", timePartition));
            try {
                executeSingleTimeRange(timePartition, configurationName);
            } catch (Exception e) {
                logger.error(String.format("an error occurred while executing on time partition=%s,configurationName=%s", timePartition, configurationName, e));
                throw e;
            }
        }
    }

    /**
     * runs calculation for single hour/day/other accumulation duration
     */
    protected abstract void executeSingleTimeRange(TimeRange timeRange, String configurationName);

}
