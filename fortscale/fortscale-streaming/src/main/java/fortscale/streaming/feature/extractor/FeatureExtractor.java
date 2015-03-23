package fortscale.streaming.feature.extractor;

import net.minidev.json.JSONObject;

public interface FeatureExtractor {

	public Object extract(JSONObject message);
}
