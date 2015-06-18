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
	public void init(String name, Config config, String eventType) {
		// get the event score field name and the minimum threshold value 
		eventScoreField = getConfigString(config, String.format("fortscale.%s.filter.%s.field", eventType, name));
		threshold = config.getDouble(String.format("fortscale.%s.filter.%s.threshold", eventType, name));
	}

	@Override
	public boolean filter(JSONObject message) {
		if (message==null)
			return true;
		
		Double score = ConversionUtils.convertToDouble(message.get(eventScoreField));
		return score==null || score < threshold;
	}

}
