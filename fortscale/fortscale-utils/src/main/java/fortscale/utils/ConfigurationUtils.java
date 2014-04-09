package fortscale.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Helper class to assist in properties configuration file 
 * holding lists of values
 */
public final class ConfigurationUtils {

	private static final String MATCHER_ARRAY_SPLIT_STRING = "#####";
	private static final String MATCHER_SPLIT_STRING = "# # #";
	
	
	public static String[][] getStringArrays(String propertyValue) {
		if (StringUtils.isEmpty(propertyValue))
			return new String[0][0];
	
		String propertySplit[] = propertyValue.split(MATCHER_ARRAY_SPLIT_STRING);
		String[][] matcherArray = new String[propertySplit.length][];
		for(int i = 0; i < propertySplit.length; i++) {
			String lineSplit[] = propertySplit[i].split(MATCHER_SPLIT_STRING);
			matcherArray[i] = lineSplit;
		}
		
		return matcherArray;
	}
	
}
