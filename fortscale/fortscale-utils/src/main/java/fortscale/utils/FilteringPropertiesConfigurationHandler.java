package fortscale.utils;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class FilteringPropertiesConfigurationHandler {

	final String DELIMITER = "%%%";

	private Map<String, String> filtering;


	@Autowired
	public FilteringPropertiesConfigurationHandler(String mapFiltering) {
		this.filtering = ConfigurationUtils.getStringMap(mapFiltering);
	}

	/**
	 * Lazy get of evidence filter
	 * @param key
	 * @return
	 */
	public CustomedFilter getFilter(String key) {

		// Getting raw value from config in a form of:
		// KEY ### OPERATOR ### VALUE
		String rawValue = filtering.get(key);
		if (rawValue == null) {
			return null;
		}

		// Spilt the raw value
		String[] filter = rawValue.split(DELIMITER);

		// Check filter structure
		if (filter.length != 3) {
			return null;
		}

		//Create CustomedFilter from raw value
		return new CustomedFilter(filter[0], filter[1], filter[2]);
	}
}
