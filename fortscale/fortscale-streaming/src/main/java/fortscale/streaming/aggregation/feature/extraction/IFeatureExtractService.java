package fortscale.streaming.aggregation.feature.extraction;

import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONObject;
import fortscale.streaming.aggregation.feature.Feature;

/**
 * Created by amira on 15/06/2015.
 */
public interface IFeatureExtractService {
    Feature extract(String featureName, JSONObject message);
    Map<String, Feature> extract(Set<String> featureNames, JSONObject message);
}
