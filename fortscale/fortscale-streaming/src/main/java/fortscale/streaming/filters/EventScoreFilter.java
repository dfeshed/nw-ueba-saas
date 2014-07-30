package fortscale.streaming.filters;

import static fortscale.streaming.ConfigUtils.getConfigString;
import net.minidev.json.JSONObject;

import org.apache.samza.config.Config;

import fortscale.utils.ConversionUtils;

/**
 * Filter message according to minimum event score
 */
public class EventScoreFilter implements MessageFilter {

	private String eventScoreField;
	private double threshold;
	
	@Override
	public void init(String name, Config config) {
		// get the event score field name and the minimum threshold value 
		eventScoreField = getConfigString(config, String.format("fortscale.filter.%s.field", name));
		threshold = config.getDouble(String.format("fortscale.filter.%s.threshold", name));
	}

	@Override
	public boolean filter(JSONObject message) {
		if (message==null)
			return true;
		
		Double score = ConversionUtils.convertToDouble(message.get(eventScoreField));
		return score==null || score < threshold;
	}

}
