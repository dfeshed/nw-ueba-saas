package fortscale.ml.model.selector;

import fortscale.utils.time.TimeRange;
import org.springframework.util.Assert;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Created by barak_schuster on 24/08/2017.
 */
public class AccumulatedSmartContextSelector implements IContextSelector {
    public static final int DAYS_BACK = 30;
    private final AccumulatedSmartContextSelectorConf accumulatedSmartContextSelectorConf;
    private final String smartRecordConfName;

    private final SmartAccumulationDataReader smartAccumulationDataReader;

    public AccumulatedSmartContextSelector(AccumulatedSmartContextSelectorConf accumulatedSmartContextSelectorConf, SmartAccumulationDataReader smartAccumulationDataReader) {
        this.accumulatedSmartContextSelectorConf = accumulatedSmartContextSelectorConf;
        this.smartAccumulationDataReader = smartAccumulationDataReader;
        this.smartRecordConfName = accumulatedSmartContextSelectorConf.getSmartRecordConfName();
        Assert.hasText(smartRecordConfName, "smartRecordConfName must be non empty");
    }

    @Override
    public Set<String> getContexts(TimeRange timeRange) {
        // todo: make it configurable
        TimeRange lastMonthTimeRange = new TimeRange(timeRange.getStart().minus(DAYS_BACK, ChronoUnit.DAYS), timeRange.getEnd());
        return smartAccumulationDataReader.findDistinctContextsByTimeRange(smartRecordConfName, lastMonthTimeRange);
    }
}
