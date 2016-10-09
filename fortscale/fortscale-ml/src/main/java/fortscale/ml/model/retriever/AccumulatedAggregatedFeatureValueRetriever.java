package fortscale.ml.model.retriever;

import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.util.GenericHistogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class AccumulatedAggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever<AccumulatedAggregatedFeatureEvent> {
    @Autowired
    private AccumulatedAggregatedFeatureEventStore store;

    public AccumulatedAggregatedFeatureValueRetriever(AccumulatedAggregatedFeatureValueRetrieverConf config) {
        super(config, true);
    }

    @Override
    protected List<AccumulatedAggregatedFeatureEvent> readObjects(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                                  String contextId,
                                                                  Date startTime,
                                                                  Date endTime) {
        return store.findAccumulatedEventsByContextIdAndTimeRange(
                aggregatedFeatureEventConf,
                contextId,
                getStartTime(endTime).toInstant(),
                endTime.toInstant()
        );
    }

    @Override
    protected void addToHistogram(GenericHistogram histogram, AccumulatedAggregatedFeatureEvent event) {
        for (Double aggregatedFeatureValue : event.getAggregatedFeatureValues()) {
            // TODO: Retriever functions should be iterated and executed here.
            histogram.add(aggregatedFeatureValue, 1d);
        }
    }
}
