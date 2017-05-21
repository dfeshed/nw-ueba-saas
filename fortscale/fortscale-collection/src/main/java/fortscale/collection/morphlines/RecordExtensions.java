package fortscale.collection.morphlines;

import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class RecordExtensions {

	private static final Logger logger = LoggerFactory.getLogger(RecordExtensions.class);
	
	public static String getStringValue(Record record, String field) throws IllegalArgumentException {
		Object value = record.getFirstValue(field);
		if (value!=null && value instanceof String) {
			return (String)value;
		} else {
			logger.debug(String.format("field %s is missing from morphline record %s", field, record.toString()));
			throw new IllegalArgumentException("field " + field + " is missing from morphlines record");
		}
	}
	
	public static String getStringValue(Record record, String field, String defaultVal) throws IllegalArgumentException  {
		try{
			return getStringValue(record, field);
		} catch(Exception e){
			return defaultVal;
		}
	}
	
	public static Long getLongValue(Record record, String field) throws IllegalArgumentException  {
		Object value = record.getFirstValue(field);
		return convertToLong(record, field, value);
	}

	public static Long getLongValue(Record record, String field, Long defaultVal) throws IllegalArgumentException  {
		try{
			return getLongValue(record, field);
		} catch(Exception e){
			return defaultVal;
		}
	}

	public static Long getLongValueFromMapField(Record record, String mapFieldName, String fieldName, Long defaultFieldVal) throws IllegalArgumentException  {
		try{
			final Map map = (Map) record.getFirstValue(mapFieldName);
			final Object value = map.get(fieldName);
			return convertToLong(record, fieldName, value);
		} catch(Exception e){
			return defaultFieldVal;
		}
	}
	
	public static Integer getIntegerValue(Record record, String field) throws IllegalArgumentException  {
		Object value = record.getFirstValue(field);
		if (value!=null && value instanceof Integer) {
			return (Integer)value;
		} else if (value!=null && value instanceof String) {
			// try to parse the string into number
			String strValue = (String)value;
			return Integer.valueOf(strValue);
		} else {
			logger.debug(String.format("field %s is missing from morphline record %s", field, record.toString()));
			throw new IllegalArgumentException("field " + field + " is missing from morphlines record");
		}
	}
	
	public static Integer getIntegerValue(Record record, String field, Integer defaultVal) throws IllegalArgumentException  {
		try{
			return getIntegerValue(record, field);
		} catch(Exception e){
			return defaultVal;
		}
	}
	
	public static Double getDoubleValue(Record record, String field) throws IllegalArgumentException  {
		Object value = record.getFirstValue(field);
		if (value!=null && value instanceof Double) {
			return (Double)value;
		} else if (value!=null && value instanceof Float) {
			Float floatValue = (Float)value;
			return floatValue.doubleValue();
		} else if (value!=null && value instanceof String) {
			// try to parse the string into number
			String strValue = (String)value;
			return Double.valueOf(strValue);
		} else {
			logger.debug(String.format("field %s is missing from morphline record %s", field, record.toString()));
			throw new IllegalArgumentException("field " + field + " is missing from morphlines record");
		}
	}
	
	public static Double getDoubleValue(Record record, String field, Double defaultVal) throws IllegalArgumentException  {
		try{
			return getDoubleValue(record, field);
		} catch(Exception e){
			return defaultVal;
		}
	}
	
	public static Boolean getBooleanValue(Record record, String field) throws IllegalArgumentException  {
		Object value = record.getFirstValue(field);
		if (value!=null && value instanceof Boolean) {
			return (Boolean)value;
		} else if (value!=null && value instanceof String) {
			// try to parse the string into number
			String strValue = (String)value;
			return Boolean.parseBoolean(strValue);
		} else {
			logger.debug(String.format("field %s is missing from morphline record %s", field, record.toString()));
			throw new IllegalArgumentException("field " + field + " is missing from morphlines record");
		}
	}

	private static Long convertToLong(Record record, String field, Object value) {
		if (value!=null && value instanceof Long) {
			return (Long)value;
		} else if (value!=null && value instanceof Integer) {
			Integer intValue = (Integer)value;
			return intValue.longValue();
		} else if (value!=null && value instanceof String) {
			// try to parse the string into number
			String strValue = (String)value;
			return Long.parseLong(strValue);
		} else {
			logger.debug(String.format("field %s is missing from morphline record %s", field, record.toString()));
			throw new IllegalArgumentException("field " + field + " is missing from morphlines record");
		}
	}
	
}
