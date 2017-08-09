package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;

import java.util.Date;
import java.util.stream.DoubleStream;


public class AggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever {

    private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

    public AggregatedFeatureValueRetriever(AggregatedFeatureValueRetrieverConf config, AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService,  AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
        super(config, aggregatedFeatureEventsConfService, false);
        this.aggregatedFeatureEventsReaderService = aggregatedFeatureEventsReaderService;
    }

    @Override
    protected DoubleStream readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                       String contextId,
                                                       Date startTime,
                                                       Date endTime) {
        return aggregatedFeatureEventsReaderService.findAggrEventsByContextIdAndTimeRange(
                aggregatedFeatureEventConf,
                contextId,
                getStartTime(endTime),
                endTime
        ).stream().mapToDouble(AggrEvent::getAggregatedFeatureValue);
    }
}
