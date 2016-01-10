package fortscale.utils.kafka;

import org.json.JSONObject;

public interface IMetricsDecider {
	boolean decide(JSONObject metrics);
}
