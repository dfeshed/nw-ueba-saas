package fortscale.aggregation.feature.functions;

import fortscale.common.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 16/06/2015.
 */
public interface IAggrFeatureEventFunctionsService {

    /**
     * Create new feature by running the associated {@link IAggrFeatureEventFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     * @param aggrFeatureEventConf the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList);

    /**
     * Returns the number of functions created and stored by this service.
     * This method is mainly used in unit tests.
     *
     * @return the number of functions.
     */
    public int getNumberOfAggrFeatureEventFunctions();
}
