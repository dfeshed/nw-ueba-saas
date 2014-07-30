package fortscale.streaming.filters;

import org.apache.samza.config.Config;
import net.minidev.json.JSONObject;


public interface MessageFilter {

	/**
	 * Initialize the filter with the filter name and configuration settings
	 */
	void init(String name, Config config);
	
	/**
	 * Determine if a message should be filtered
	 */
	boolean filter(JSONObject message);
}
