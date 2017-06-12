package fortscale.utils.fixedduration;

import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;

import java.util.List;

/**
 * abstract class that handles data processing for {@link TimeRange}
 *
 * Created by barak_schuster on 6/11/17.
 */
public abstract class FixedDurationStrategyExecutor {
    private static final Logger logger = Logger.getLogger(FixedDurationStrategyExecutor.class);

    private final FixedDurationStrategy strategy;

    public FixedDurationStrategyExecutor(FixedDurationStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * brakes given time range to smaller partitions by {@link this#strategy} and execute upon them
     * @param timeRange start and end time of data to be executed upon
     * @param dataSource
     */
    public void execute(TimeRange timeRange, String dataSource) {
        logger.debug("got execution time range={}",timeRange);
        List<TimeRange> partitionedTimeRanges = FixedDurationStrategyUtils.splitTimeRangeByStrategy(timeRange, strategy);
        for (TimeRange timePartition: partitionedTimeRanges) {
            logger.debug("executing on time partition={}",timePartition);
            executeSingleTimeRange(timeRange,dataSource);
        }
    }

    /**
     * runs calculation for single hour/day/other fixed duration
     */
    public abstract void executeSingleTimeRange(TimeRange timeRange, String dataSource);




}
