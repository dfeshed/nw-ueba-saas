package fortscale.ml.model.retriever;

import fortscale.aggregation.exceptions.InvalidAggregatedFeatureEventConfNameException;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.retriever.metrics.AggregatedFeatureValueRetrieverMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.DoubleStream;

@Configurable(preConstruction = true)
public abstract class AbstractAggregatedFeatureValueRetriever extends AbstractDataRetriever {
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private StatsService statsService;

    private AggregatedFeatureEventConf aggregatedFeatureEventConf;
    private AggregatedFeatureValueRetrieverMetrics metrics;

    public AbstractAggregatedFeatureValueRetriever(AbstractAggregatedFeatureValueRetrieverConf config,
                                                   boolean isAccumulation) {
        super(config);
        String aggregatedFeatureEventConfName = config.getAggregatedFeatureEventConfName();
        aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
                .getAggregatedFeatureEventConf(aggregatedFeatureEventConfName);
		if (aggregatedFeatureEventConf == null) {
			throw new InvalidAggregatedFeatureEventConfNameException(config.getAggregatedFeatureEventConfName());
		}
        metrics = new AggregatedFeatureValueRetrieverMetrics(statsService, aggregatedFeatureEventConfName, isAccumulation);
    }

    protected abstract DoubleStream readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                                String contextId,
                                                                Date startTime,
                                                                Date endTime);

    @Override
    public Object retrieve(String contextId, Date endTime) {
        metrics.retrieve++;
        DoubleStream aggregatedFeatureValues = readAggregatedFeatureValues(
                aggregatedFeatureEventConf,
                contextId,
                getStartTime(endTime),
                endTime
        );
        GenericHistogram reductionHistogram = new GenericHistogram();
        aggregatedFeatureValues.forEach(aggregatedFeatureValue -> {
            metrics.aggregatedFeatureValues++;
            // TODO: Retriever functions should be iterated and executed here.
            reductionHistogram.add(aggregatedFeatureValue, 1d);
        });
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
        metrics.getContextId++;
        Assert.notEmpty(context);
        return AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
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
}
