package fortscale.streaming.service;

import net.minidev.json.JSONObject;

public interface Scorer {
	public Double calculateScore(JSONObject jsonObject) throws Exception;
}
