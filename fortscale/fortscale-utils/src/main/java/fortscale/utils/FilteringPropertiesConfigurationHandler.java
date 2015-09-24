package fortscale.utils;

import java.util.Map;

public class FilteringPropertiesConfigurationHandler {

	final String DELIMITER = "%%%";

	private Map<String, String> filtering;

	public FilteringPropertiesConfigurationHandler(Map<String, String> filtering) {
		this.filtering = filtering;
	}

	/**
	 * Lazy get of evidence filter
	 * @param key
	 * @return
	 */
	public EvidenceFilter getFilter(String key) {

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

		//Create EvidenceFilter from raw value
		return new EvidenceFilter(filter[0], filter[1], filter[2]);
	}
}
