package fortscale.ml.model.retriever;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.stream.DoubleStream;

@Configurable(preConstruction = true)
public class AccumulatedAggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever {
    @Autowired
    private AccumulatedAggregatedFeatureEventStore store;

    public AccumulatedAggregatedFeatureValueRetriever(AccumulatedAggregatedFeatureValueRetrieverConf config) {
        super(config, true);
    }

    @Override
    protected DoubleStream readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                       String contextId,
                                                       Date startTime,
                                                       Date endTime) {
        return store.findAccumulatedEventsByContextIdAndTimeRange(
                aggregatedFeatureEventConf,
                contextId,
                getStartTime(endTime).toInstant(),
                endTime.toInstant()
        ).stream().flatMapToDouble(accAggEvent -> accAggEvent
                .getAggregatedFeatureValues()
                .stream()
                .mapToDouble(v -> v));
    }
}
