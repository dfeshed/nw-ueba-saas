package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

import java.util.Date;
import java.util.stream.DoubleStream;

public class AccumulatedAggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever {

    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;


    public AccumulatedAggregatedFeatureValueRetriever(AccumulatedAggregatedFeatureValueRetrieverConf config, AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
        super(config, aggregatedFeatureEventsConfService, true);
        this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
    }

    @Override
    protected DoubleStream readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                       String contextId,
                                                       Date startTime,
                                                       Date endTime) {
        return aggregationEventsAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(
                aggregatedFeatureEventConf.getName(),
                contextId,
                getStartTime(endTime).toInstant(),
                endTime.toInstant()
        ).stream().flatMapToDouble(accAggEvent -> accAggEvent
                .getAggregatedFeatureValues()
                .stream()
                .mapToDouble(v -> v));
    }
}
