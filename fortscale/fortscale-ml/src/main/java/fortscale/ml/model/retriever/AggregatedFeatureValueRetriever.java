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

import java.util.*;

@Configurable(preConstruction = true)
public class AggregatedFeatureValueRetriever extends AbstractDataRetriever {
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;
    @Autowired
    private StatsService statsService;

    private AggregatedFeatureEventConf aggregatedFeatureEventConf;
    private AggregatedFeatureValueRetrieverMetrics metrics;

    public AggregatedFeatureValueRetriever(AggregatedFeatureValueRetrieverConf config) {
        super(config);
        String aggregatedFeatureEventConfName = config.getAggregatedFeatureEventConfName();
        aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
                .getAggregatedFeatureEventConf(aggregatedFeatureEventConfName);
        metrics = new AggregatedFeatureValueRetrieverMetrics(statsService, aggregatedFeatureEventConfName);
        validate(config);
    }

    private void validate(AggregatedFeatureValueRetrieverConf config) {
        if (aggregatedFeatureEventConf == null) {
            throw new InvalidAggregatedFeatureEventConfNameException(config.getAggregatedFeatureEventConfName());
        }
    }

    @Override
    public Object retrieve(String contextId, Date endTime) {
        metrics.retrieve++;
        List<AggrEvent> aggrEvents = aggregatedFeatureEventsReaderService
                .findAggrEventsByContextIdAndTimeRange(
                aggregatedFeatureEventConf, contextId, getStartTime(endTime), endTime);
        metrics.aggregatedFeatureEvents += aggrEvents.size();
        GenericHistogram reductionHistogram = new GenericHistogram();

        for (AggrEvent aggrEvent : aggrEvents) {
            Double aggregatedFeatureValue = aggrEvent.getAggregatedFeatureValue();
            // TODO: Retriever functions should be iterated and executed here.
            reductionHistogram.add(aggregatedFeatureValue, 1d);
        }

        return reductionHistogram.getN() > 0 ? reductionHistogram : null;
    }

    @Override
    public Object retrieve(String contextId, Date endTime, Feature feature) {
        throw new UnsupportedOperationException(String.format(
                "%s does not support retrieval of a single feature",
                getClass().getSimpleName()));
    }

    @Override
    public Set<String> getEventFeatureNames() {
        metrics.getEventFeatureNames++;
        Set<String> set = new HashSet<>(1);
        set.add(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_VALUE);
        return set;
    }

    @Override
    public List<String> getContextFieldNames() {
        metrics.getContextFieldNames++;
        return aggregatedFeatureEventConf.getBucketConf().getContextFieldNames();
    }

    @Override
    public String getContextId(Map<String, String> context) {
        metrics.getContextId++;
        Assert.notEmpty(context);
        return AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
    }
}
