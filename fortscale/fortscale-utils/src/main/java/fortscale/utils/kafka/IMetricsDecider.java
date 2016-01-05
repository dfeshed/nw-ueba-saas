package fortscale.utils.kafka;

import org.json.JSONObject;

import java.util.Map;

public interface IMetricsDecider {
	public boolean decide(JSONObject metrics);
}
