package fortscale.utils.fixedduration;

import fortscale.utils.logging.Logger;
import fortscale.utils.store.record.StoreManagerMetadataProperties;
import fortscale.utils.time.TimeRange;

import java.util.List;

/**
 * abstract class that handles data processing for {@link TimeRange}
 *
 * Created by barak_schuster on 6/11/17.
 */
public abstract class FixedDurationStrategyExecutor {
    private static final Logger logger = Logger.getLogger(FixedDurationStrategyExecutor.class);

    protected final FixedDurationStrategy strategy;

    public FixedDurationStrategyExecutor(FixedDurationStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * brakes given time range to smaller partitions by {@link this#strategy} and execute upon them
     * @param timeRange start and end time of data to be executed upon
     * @param adeEventType ade event type
     */
    public void execute(TimeRange timeRange, String adeEventType, StoreManagerMetadataProperties storeManagerMetadataProperties) {
        logger.debug("got execution time range={}",timeRange);
        List<TimeRange> partitionedTimeRanges = FixedDurationStrategyUtils.splitTimeRangeByStrategy(timeRange, strategy);

        for(String contextType: getDistinctContextTypes(adeEventType)) {
            for (TimeRange timePartition : partitionedTimeRanges) {
                logger.debug("executing on time partition={}", timePartition);
                try {
                    executeSingleTimeRange(timePartition, adeEventType,contextType, storeManagerMetadataProperties);
                }
                catch (Exception e)
                {
                    logger.error("an error occurred while executing on time partition={},adeEventType={},contextType={}",timePartition,adeEventType,contextType,e);
                    throw e;
                }
            }
        }
    }

    /**
     * runs calculation for single hour/day/other fixed duration per adeEventType for all relvant contexts
     */
    protected abstract void executeSingleTimeRange(TimeRange timeRange, String adeEventType, String contextType, StoreManagerMetadataProperties storeManagerMetadataProperties);

    protected abstract List<String> getDistinctContextTypes(String adeEventType);
}
