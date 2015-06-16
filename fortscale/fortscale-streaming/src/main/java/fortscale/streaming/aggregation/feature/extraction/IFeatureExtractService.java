package fortscale.streaming.aggregation.feature.extraction;

import fortscale.streaming.aggregation.feature.Feature;
import net.minidev.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 15/06/2015.
 */
public interface IFeatureExtractService {
    Feature extract(String featureName, JSONObject message);
    Map<String, Feature> extract(List<String> featureNames, JSONObject message);
}
