package fortscale.streaming.filters;

import org.apache.samza.config.Config;
import net.minidev.json.JSONObject;


public interface MessageFilter {


	/**
	 * Determine if a message should be filtered
	 */
	boolean filter(JSONObject message);

	String getName();
	void setName(String name);

	boolean monitorIfFiltered();
}
