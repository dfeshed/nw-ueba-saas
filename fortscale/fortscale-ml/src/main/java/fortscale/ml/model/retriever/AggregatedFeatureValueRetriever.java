package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.stream.DoubleStream;

@Configurable(preConstruction = true)
public class AggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever {
    @Autowired
    private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

    public AggregatedFeatureValueRetriever(AggregatedFeatureValueRetrieverConf config) {
        super(config, false);
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
