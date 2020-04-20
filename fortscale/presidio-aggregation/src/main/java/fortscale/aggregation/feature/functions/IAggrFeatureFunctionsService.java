package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.common.feature.Feature;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Amir Ahinoam
 */
public interface IAggrFeatureFunctionsService {
    /**
     * Updates the aggrFeatures by running the associated {@link IAggrFeatureFunction} that is configured for each
     * AggrFeature in the given  {@link AggregatedFeatureConf} and using the features as input to those functions.
     * Creates new map entry <String, Feature> for any AggrFeatureConf for which there is no entry in the aggrFeatures
     * map.
     *
     * @return a map with entry for each {@link AggregatedFeatureConf}. Each entry is updated by the relevant function.
     * If aggrFeatures is null, a new {@link HashMap <String, Feature>} will be created with new Feature object for each
     * of the {@link AggregatedFeatureConf} in aggrFeatureConfs.
     */
    Map<String, Feature> updateAggrFeatures(AdeRecordReader adeRecordReader, List<AggregatedFeatureConf> aggrFeatureConfs, Map<String, Feature> aggrFeatures, Map<String, Feature> features);
}
