package fortscale.utils;

import fortscale.utils.spring.SpringPropertiesUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Filter events related to evidence according to anomaly value.
 * E.g. evidence is high_number_of_success_ntlm and the anomaly value is failure_code, so the filter should return only
 * the events that their failure_code is 0x0, and not all of them.
 * The filters themselves defined in : evidence.events.filtering.properties
 */
public class FilteringPropertiesConfigurationHandler {

	final String DELIMITER = "%%%";
	final String VARIABLE_ANNOTATION_PREFIX = "{{";
	final String VARIABLE_ANNOTATION_SUFFIX = "}}";

	/**
	 * Lazy get of evidence filter
	 * @param key
	 * @return
	 */
	public List<CustomedFilter> getFilter(String key, Map<String, String> evidenceMap) {

		// Getting raw value from config in a form of:
		// KEY %%%OPERATOR%%%VALUE
		Map<String, String> filterProperties = SpringPropertiesUtil.getPropertyMapByPrefix(key);
		List<CustomedFilter> filters = new ArrayList<>();

		filterProperties.values().stream().filter(StringUtils::isNotEmpty).forEach(rawValue -> {
			// Spilt the raw value
			String[] filter = rawValue.split(DELIMITER);

			// Check filter structure
			if (filter.length == 3) {
				//if filter contains a value of type {{somevalue}} then strip the {{ }} from the filter and get the value of the evidence field with that name
				if (filter[2].startsWith(VARIABLE_ANNOTATION_PREFIX) && filter[2].endsWith(VARIABLE_ANNOTATION_SUFFIX)) {
					String field = filter[2].substring(VARIABLE_ANNOTATION_PREFIX.length(), filter[2].length() -
							VARIABLE_ANNOTATION_SUFFIX.length());
					if (evidenceMap != null && evidenceMap.containsKey(field)) {
						filter[2] = evidenceMap.get(field);
					}
				}
			}

			//Create CustomedFilter from raw value
			filters.add(new CustomedFilter(filter[0], filter[1], filter[2]));
		});

		return filters;
	}
}
