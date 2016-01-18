package fortscale.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Type conversion utility methods 
 */
public final class ConversionUtils {

	private static final String CSV_DELIMITER = ",";
	private static final String WHITESPACE_DELIMITER_REGEX = "\\s+"; // i.e. one or more whitespace chars
	private static final String EMPTY_STR = "";

	public static Long convertToLong(Object value) {
		try {
			if (value==null)
				return null;
			
			if (value instanceof Long)
				return (Long)value;
			
			if (value instanceof Integer)
				return ((Integer) value).longValue();
			
			String str = value.toString();
			return Long.valueOf(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Integer convertToInteger(Object value) {
		try {
			if (value==null)
				return null;
			
			if (value instanceof Integer)
				return (Integer)value;
			
			if (value instanceof Long)
				return ((Long)value).intValue();
			
			String str = value.toString();
			return Integer.valueOf(str);				
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Double convertToDouble(Object value) {
		try {
			if (value==null)
				return null;
			
			if (value instanceof Double)
				return (Double)value;
			
			if (value instanceof Integer)
				return ((Integer)value).doubleValue();
			
			if (value instanceof Long)
				return ((Long)value).doubleValue();
			
			String str = value.toString();
			return Double.valueOf(str);
		} catch (Exception e) {
			return null;
		}
	}
	

	public static String convertToString(Object value) {
		if (value==null)
			return null;
		else
			return value.toString();
	}
	
	public static Boolean convertToBoolean(Object value) {
		return convertToBoolean(value, false);

	}
	public static Boolean convertToBoolean(Object value, boolean defaultValue) {
		if (value==null)
			return defaultValue;
		
		try {
			if (value instanceof Boolean)
				return (Boolean)value;
			
			if (value instanceof String)
				return Boolean.valueOf((String)value);
			
		} catch (Exception e) {}
		return defaultValue;
		
	}


	/*
	 * Converts the CSV-formatted field to a map of key-value ("1,2" --->{1:2}).
     */
	public static Map<String, String> convertCSVToMap(String fieldsCSV, String delimiter) {
		Map<String, String> mappedCSV = new LinkedHashMap<>(); // to preserve insertion order

		if (fieldsCSV != null) {
			String[] fieldsArray = fieldsCSV.split(CSV_DELIMITER);
			for (String fieldDef : fieldsArray) {
				if (!EMPTY_STR.equals(fieldDef)) {
					fieldDef = fieldDef.trim();
					String[] fieldDefSep = fieldDef.split(WHITESPACE_DELIMITER_REGEX);
					fieldSchema.put(fieldDefSep[0], fieldDefSep[1]);
				}
			}
		}

	/*
 * Converts the CSV-formatted fields to a list of strings ("1,2,3,4" ---> [1,2,3,4]).
 */
	public static List<String> convertCSVToList(String fieldsCSV, String delimiter) {
		List<String> ListedCSV = new ArrayList<>(); // to preserve insertion order

		if (fieldsCSV != null) {
			String[] fieldsArray = fieldsCSV.split(delimiter);
			for (String field : fieldsArray)
				ListedCSV.add(field);

		}

		return ListedCSV;
	}


}
