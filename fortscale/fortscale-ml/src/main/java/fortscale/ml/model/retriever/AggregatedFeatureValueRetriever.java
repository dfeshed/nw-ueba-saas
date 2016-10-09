package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.common.util.GenericHistogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.stream.DoubleStream;

@Configurable(preConstruction = true)
public class AggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever<AggrEvent> {
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

    @Override
    public String getContextId(Map<String, String> context) {
        metrics.getContextId++;
        Assert.notEmpty(context);
        return AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
    }
}
