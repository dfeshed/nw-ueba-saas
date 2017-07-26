package fortscale.ml.model.retriever;

import fortscale.aggregation.exceptions.InvalidAggregatedFeatureEventConfNameException;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public abstract class AbstractAggregatedFeatureValueRetriever extends AbstractDataRetriever {

    private final String CONTEXT_FIELD = "context";

    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    private AggregatedFeatureEventConf aggregatedFeatureEventConf;

    public AbstractAggregatedFeatureValueRetriever(AbstractAggregatedFeatureValueRetrieverConf config,
                                                   AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
                                                   boolean isAccumulation) {
        super(config);
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        String aggregatedFeatureEventConfName = config.getAggregatedFeatureEventConfName();
        aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
                .getAggregatedFeatureEventConf(aggregatedFeatureEventConfName);
        if (aggregatedFeatureEventConf == null) {
            throw new InvalidAggregatedFeatureEventConfNameException(config.getAggregatedFeatureEventConfName());
        }
    }

    protected abstract DoubleStream readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                                String contextId,
                                                                Date startTime,
                                                                Date endTime);

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {

        //TODO: metrics.retrieve++;
        DoubleStream aggregatedFeatureValues = readAggregatedFeatureValues(
                aggregatedFeatureEventConf, contextId, getStartTime(endTime), endTime);
        GenericHistogram reductionHistogram = new GenericHistogram();
        final boolean[] noDataInDatabase = {true};

        aggregatedFeatureValues.forEach(aggregatedFeatureValue -> {
            noDataInDatabase[0] = false;
            //TODO: metrics.aggregatedFeatureValues++;
            // TODO: Retriever functions should be iterated and executed here.
            reductionHistogram.add(aggregatedFeatureValue, 1d);
        });

        if (reductionHistogram.getN() == 0) {
            if (noDataInDatabase[0]) {
                return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
            } else {
                return new ModelBuilderData(NoDataReason.ALL_DATA_FILTERED);
            }
        } else {
            return new ModelBuilderData(reductionHistogram);
        }
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
        return AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
    }

    @Override
    public Set<String> getEventFeatureNames() {
        //TODO: metrics.getEventFeatureNames++;
        Set<String> set = new HashSet<>(1);
        set.add(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_VALUE);
        return set;
    }

    @Override
    public List<String> getContextFieldNames() {
        //TODO: metrics.getContextFieldNames++;
        List<String> contextFieldNames = aggregatedFeatureEventConf.getBucketConf().getContextFieldNames();
        return contextFieldNames.stream().map(c -> CONTEXT_FIELD + "." + c).collect(Collectors.toList());
    }
}
