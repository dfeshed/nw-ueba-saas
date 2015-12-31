package fortscale.utils.kafka;

import org.json.JSONObject;

public interface IMetricsDecider {
	public boolean decide(JSONObject metrics);
}
