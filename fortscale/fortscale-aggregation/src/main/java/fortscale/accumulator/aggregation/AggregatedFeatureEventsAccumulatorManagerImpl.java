package fortscale.accumulator.aggregation;

import com.google.common.collect.Sets;
import fortscale.accumulator.accumulator.Accumulator;
import fortscale.accumulator.manager.AccumulatorManagerImpl;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;

import java.util.Set;

/**
 * Created by barak_schuster on 10/9/16.
 */
public class AggregatedFeatureEventsAccumulatorManagerImpl  extends AccumulatorManagerImpl{
    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    /**
     * C'tor
     *
     * @param accumulator accumulator to execute by
     * @see AggregatedFeatureEventsAccumulator
     */
    public AggregatedFeatureEventsAccumulatorManagerImpl(Accumulator accumulator,
                                                         AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
        super(accumulator);
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
    }

    @Override
    protected Set<String> getFeatureNames() {
        return Sets.newHashSet(aggregatedFeatureEventsConfService.getAggrFeatureEventNameList());
    }
}
