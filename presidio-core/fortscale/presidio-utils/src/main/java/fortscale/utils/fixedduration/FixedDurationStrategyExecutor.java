package fortscale.utils.fixedduration;

import com.google.common.collect.TreeMultiset;
import fortscale.utils.logging.Logger;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public void execute(TimeRange timeRange, String adeEventType, StoreMetadataProperties storeMetadataProperties) {
        logger.debug("got execution time range={}",timeRange);
        List<TimeRange> partitionedTimeRanges = FixedDurationStrategyUtils.splitTimeRangeByStrategy(timeRange, strategy);

        List<String> contextFieldNamesToExclude = new ArrayList<>();
        for(String contextType: getDistinctContextTypes(adeEventType, strategy)) {
            for (TimeRange timePartition : partitionedTimeRanges) {
                logger.debug("executing on time partition={}", timePartition);
                try {
                    executeSingleTimeRange(timePartition, adeEventType, contextType, contextFieldNamesToExclude, storeMetadataProperties);
                }
                catch (Exception e)
                {
                    logger.error("an error occurred while executing on time partition={},adeEventType={},contextType={}," +
                                    "contextFieldNamesToExclude={}",
                            timePartition,adeEventType,contextType,contextFieldNamesToExclude,e);
                    throw e;
                }
            }
            contextFieldNamesToExclude.add(contextType);
        }
    }

    /**
     * runs calculation for single hour/day/other fixed duration per adeEventType for all relvant contexts
     */
    protected abstract void executeSingleTimeRange(TimeRange timeRange, String adeEventType, String contextType, List<String> contextFieldNamesToExclude, StoreMetadataProperties storeMetadataProperties);

    protected abstract List<List<String>> getListsOfContextFieldNames(String adeEventType, FixedDurationStrategy strategy);

    protected List<String> getDistinctContextTypes(String adeEventType, FixedDurationStrategy strategy){
        //Returns a list that contain the minimum number of contexts which needed to build all buckets.

        List<List<String>> confsContextsFieldNames = getListsOfContextFieldNames(adeEventType, strategy);
        List<String> ret = new ArrayList<>();
        while(confsContextsFieldNames.size() > 0){
            TreeMultiset<String> contextTreeMultiSet = TreeMultiset.create();
            confsContextsFieldNames.forEach(contextsFieldNames -> contextTreeMultiSet.addAll(contextsFieldNames));

            String  context = contextTreeMultiSet.lastEntry().getElement();
            ret.add(context);

            confsContextsFieldNames = confsContextsFieldNames.stream()
                    .filter(contextsFieldNames -> !contextsFieldNames.contains(context))
                    .collect(Collectors.toList());
        }

        return ret;
    }
}
