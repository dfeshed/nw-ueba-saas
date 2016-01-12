package fortscale.utils;

import java.util.Map;

/**
 * Type conversion utility methods 
 */
public final class ConversionUtils {

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


	public static void splitCSVtoMap(String fieldsCsv, Map<String, String> feldSchema, String SplitingSimbol) {

		if (fieldsCsv != null) {
			String[] fieldsArray = fieldsCsv.split(SplitingSimbol);
			for (String fieldDef : fieldsArray) {
				String[] fieldDefSep = fieldDef.split(" ");
				feldSchema.put(fieldDefSep[0], fieldDefSep[1]);
				//this.enrichFelds.put(fieldDefSep[0],fieldDefSep[1]);
				//this.scoreFelds.put(fieldDefSep[0],fieldDefSep[1]);
			}
		}
	}
	
		
}
