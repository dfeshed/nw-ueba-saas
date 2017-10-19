package fortscale.ml.model.retriever;

import fortscale.aggregation.exceptions.InvalidAggregatedFeatureEventConfNameException;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.feature.Feature;
import fortscale.ml.model.ModelBuilderData;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Instant;
import java.util.*;


public abstract class AbstractAggregatedFeatureValueRetriever extends AbstractDataRetriever {

    private AggregatedFeatureEventConf aggregatedFeatureEventConf;

    public AbstractAggregatedFeatureValueRetriever(AbstractAggregatedFeatureValueRetrieverConf config,
                                                   AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
                                                   boolean isAccumulation) {
        super(config);
        String aggregatedFeatureEventConfName = config.getAggregatedFeatureEventConfName();
        aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
                .getAggregatedFeatureEventConf(aggregatedFeatureEventConfName);
        if (aggregatedFeatureEventConf == null) {
            throw new InvalidAggregatedFeatureEventConfNameException(config.getAggregatedFeatureEventConfName());
        }
    }

    protected abstract TreeMap<Instant, Double> readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                                        String contextId,
                                                                        Date startTime,
                                                                        Date endTime);

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        Map<Instant, Double> InstantToaggregatedFeatureValues = readAggregatedFeatureValues(
                aggregatedFeatureEventConf, contextId, getStartTime(endTime), endTime);

        return new ModelBuilderData(InstantToaggregatedFeatureValues);
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        throw new UnsupportedOperationException(String.format(
                "%s does not support retrieval of a single feature",
                getClass().getSimpleName()));
    }

    @Override
    public String getContextId(Map<String, String> context) {
        //TODO: metrics.getContextId++;
        Assert.notEmpty(context);
        return AdeContextualAggregatedRecord.getAggregatedFeatureContextId(context);
    }

    @Override
    public Set<String> getEventFeatureNames() {
        //TODO: metrics.getEventFeatureNames++;
        Set<String> set = new HashSet<>(1);
        set.add(AdeAggregationRecord.FEATURE_VALUE_FIELD_NAME);
        return set;
    }

    @Override
    public List<String> getContextFieldNames() {
        //TODO: metrics.getContextFieldNames++;
        return aggregatedFeatureEventConf.getBucketConf().getContextFieldNames();

    }
}
