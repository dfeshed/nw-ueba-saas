package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.aggregation.Exceptions.InvalidAggregatedFeatureEventConfNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Configurable(preConstruction = true)
public class AggregatedFeatureValueRetriever extends AbstractDataRetriever {
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

    private AggregatedFeatureEventConf aggregatedFeatureEventConf;
    public AggregatedFeatureValueRetriever(AggregatedFeatureValueRetrieverConf config) {
        super(config);
        String aggregatedFeatureEventConfName = config.getAggregatedFeatureEventConfName();
        aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
                .getAggregatedFeatureEventConf(aggregatedFeatureEventConfName);
        validate(config);
    }

    private void validate(AggregatedFeatureValueRetrieverConf config) {
        if (aggregatedFeatureEventConf == null)
            throw new InvalidAggregatedFeatureEventConfNameException(config.getAggregatedFeatureEventConfName());
    }

    @Override
    public Object retrieve(String contextId, Date endTime) {
        List<AggrEvent> aggrEvents = aggregatedFeatureEventsReaderService
                .findAggrEventsByContextIdAndTimeRange(
                        aggregatedFeatureEventConf, contextId, getStartTime(endTime), endTime);
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
    public String getContextId(Map<String, String> context) {
        Assert.notEmpty(context);
        return AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
    }

    @Override
    public Set<String> getEventFeatureNames() {
        Set<String> set = new HashSet<>(1);
        set.add(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_VALUE);
        return set;
    }

    @Override
    public List<String> getContextFieldNames() {
        return aggregatedFeatureEventConf.getBucketConf().getContextFieldNames().stream()
                .map(contextFieldName -> String.format("%s.%s", AggrEvent.EVENT_FIELD_CONTEXT, contextFieldName))
                .collect(Collectors.toList());
    }
}
