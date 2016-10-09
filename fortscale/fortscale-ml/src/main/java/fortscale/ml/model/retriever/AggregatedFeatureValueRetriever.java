package fortscale.ml.model.retriever;

import fortscale.aggregation.exceptions.InvalidAggregatedFeatureEventConfNameException;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.retriever.metrics.AggregatedFeatureValueRetrieverMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class AggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever<AggrEvent> {
    @Autowired
    private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

    public AggregatedFeatureValueRetriever(AggregatedFeatureValueRetrieverConf config) {
        super(config, false);
    }

    @Override
    protected List<AggrEvent> readObjects(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                          String contextId,
                                          Date startTime,
                                          Date endTime) {
        return aggregatedFeatureEventsReaderService.findAggrEventsByContextIdAndTimeRange(
                aggregatedFeatureEventConf,
                contextId,
                getStartTime(endTime),
                endTime
        );
    }

    @Override
    protected void addToHistogram(GenericHistogram histogram, AggrEvent event) {
        Double aggregatedFeatureValue = event.getAggregatedFeatureValue();
        // TODO: Retriever functions should be iterated and executed here.
        histogram.add(aggregatedFeatureValue, 1d);
    }

    @Override
    public String getContextId(Map<String, String> context) {
        metrics.getContextId++;
        Assert.notEmpty(context);
        return AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
    }
}
