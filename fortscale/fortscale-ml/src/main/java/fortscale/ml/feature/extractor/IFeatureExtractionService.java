package fortscale.ml.feature.extractor;

import net.minidev.json.JSONObject;

public interface IFeatureExtractionService {

	public Object extract(String featureName, JSONObject eventMessage);
}
