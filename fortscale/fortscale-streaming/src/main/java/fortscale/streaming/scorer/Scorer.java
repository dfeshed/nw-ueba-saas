package fortscale.streaming.scorer;

import net.minidev.json.JSONObject;

public interface Scorer {
	public Double calculateScore(JSONObject jsonObject) throws Exception;
}
