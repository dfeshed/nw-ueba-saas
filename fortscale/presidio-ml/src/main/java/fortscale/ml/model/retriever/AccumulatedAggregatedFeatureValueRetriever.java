package fortscale.ml.model.retriever;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.DoubleStream;

public class AccumulatedAggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever {

    private AccumulatedAggregatedFeatureEventStore store;


    public AccumulatedAggregatedFeatureValueRetriever(AccumulatedAggregatedFeatureValueRetrieverConf config, AccumulatedAggregatedFeatureEventStore store, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
        super(config, aggregatedFeatureEventsConfService, true);
        this.store = store;
    }

    @Override
    protected DoubleStream readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                       String contextId,
                                                       Date startTime,
                                                       Date endTime) {
        return store.findAccumulatedEventsByContextIdAndStartTimeRange(
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
