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
	private String name;
	private boolean monitorIfFiltered;

	public EventScoreFilter(String eventScoreField, double threshold, String name, boolean monitorIfFiltered) {
		// get the event score field name and the minimum threshold value 
		this.eventScoreField = eventScoreField;
		this.threshold = threshold;
		this.name = name;
		this.monitorIfFiltered = monitorIfFiltered;
	}

	@Override
	public boolean filter(JSONObject message) {
		if (message==null)
			return true;
		
		Double score = ConversionUtils.convertToDouble(message.get(eventScoreField));
		return score==null || score < threshold;
	}

	@Override
	public String getName(){
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean monitorIfFiltered() {
		return this.monitorIfFiltered;
	}


	public void setMonitorIfFiltered(boolean monitorIfFiltered) {
		this.monitorIfFiltered = monitorIfFiltered;
	}
}
