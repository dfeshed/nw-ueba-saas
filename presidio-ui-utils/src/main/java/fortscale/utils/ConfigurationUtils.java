package fortscale.utils;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

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

	public static Map getStringMap(String propertyValue){
		Map propertyMap = new HashMap();
		if (StringUtils.isEmpty(propertyValue))
			return propertyMap;

		String[] propertySplit = propertyValue.split(MATCHER_ARRAY_SPLIT_STRING);
		for(int i = 0; i < propertySplit.length; i++) {
			String lineSplit[] = propertySplit[i].split(MATCHER_SPLIT_STRING);
			propertyMap.put(lineSplit[0],lineSplit[1]);
		}
		return propertyMap;
	}
	
}
