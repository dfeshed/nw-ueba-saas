package fortscale.utils;

import java.util.*;
import java.util.stream.Collectors;

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
	public static Map<String, String> convertCSVToMap(String csvString) {
		Map<String, String> mappedCSV = new LinkedHashMap<>(); // to preserve insertion order

		if (csvString != null && !EMPTY_STR.equals(csvString)) {
			String[] fieldsArray = csvString.split(CSV_DELIMITER);
			for (String fieldDef : fieldsArray) {
				if (!EMPTY_STR.equals(fieldDef)) {
					fieldDef = fieldDef.trim();
					String[] fieldDefSep = fieldDef.split(WHITESPACE_DELIMITER_REGEX);
					mappedCSV.put(fieldDefSep[0], fieldDefSep[1]);
				}
			}
		}

		return mappedCSV;
	}

	public static Set<String> convertCSVToSet(String csvString, boolean keepOrder) {
		if (csvString == null || csvString.isEmpty()) {
			return new HashSet<>();
		}

		String[] fieldsArray = csvString.split(CSV_DELIMITER);

		if (!keepOrder) {
			return Arrays.asList(fieldsArray).stream().collect(Collectors.toSet());
		}
		else {
			return Arrays.asList(fieldsArray).stream().collect(Collectors.toCollection(LinkedHashSet::new));
		}
	}

	public static Set<String> convertCSVToSet(String csvString) {
		return convertCSVToSet(csvString, false);
	}

	/*
 * Converts String that is split by some delimiter  to a list of strings ("1,2,3,4" ---> [1,2,3,4]).
 */
	public static List<String> convertStringToList(String str, String delimiter)
	{
		List<String> ListedSTR = new ArrayList<>(); // to preserve insertion order
		if (str != null) {
			String[] fieldsArray = str.split(delimiter);
			Collections.addAll(ListedSTR, fieldsArray);
		}

		return ListedSTR;
	}

	public static Map<String,String> splitCSVtoMap (String CSVfield)
	{
		Map<String,String> result = new LinkedHashMap<>();
		List<String> csvAsList = ConversionUtils.convertStringToList(CSVfield,CSV_DELIMITER);
		for (String keyValuePair : csvAsList)
		{
			Map<String,String> res;
			res  = ConversionUtils.convertCSVToMap(keyValuePair);
			result.putAll(res);
		}

		return result;
	}
}
