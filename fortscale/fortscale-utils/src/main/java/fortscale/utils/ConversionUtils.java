package fortscale.utils;

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
			
			String str = value.toString();
			return Long.valueOf(str);
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
	
	
		
}
