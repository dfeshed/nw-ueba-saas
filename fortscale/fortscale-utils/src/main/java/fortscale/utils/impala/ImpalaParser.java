package fortscale.utils.impala;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ImpalaParser {
	
	public static final String IMPALA_NULL_VALUE = "\\N";
	
	private static final String TABLE_FIELD_DEFINITION_DELIMITER = ",";
	private static final String FIELD_DEFINITION_DELIMITER = " ";
	
	public Date parseTimeDate(String dateString) throws ParseException {
		return ImpalaDateTime.parseTimeDate(dateString);
	}
	
	public String formatTimeDate(Date date) {
		return ImpalaDateTime.formatTimeDate(date);
	}
	
	public DateTime parseTimeDateToDateTime(String dateString) throws ParseException {
		return ImpalaDateTime.parseTimeDateToDateTime(dateString);
	}
	
	public String formatTimeDate(DateTime dateTime) {
		return ImpalaDateTime.formatTimeDate(dateTime);
	}
	
	public long getRuntime(Date timestamp){
		return timestamp.getTime()/1000;
	}
	
	public static List<String> getTableFieldNames(String tableFieldDefinition){
		String tableFieldDefinitionSplit[] = tableFieldDefinition.split(TABLE_FIELD_DEFINITION_DELIMITER);
		List<String> ret = new ArrayList<>(tableFieldDefinitionSplit.length);
		for(String fieldDef: tableFieldDefinitionSplit){
			fieldDef = fieldDef.trim();
			String fieldDefSplit[] = fieldDef.split(FIELD_DEFINITION_DELIMITER);
			ret.add(fieldDefSplit[0]);
		}
		
		return ret;
	}
	
	public static String[] getTableFieldNamesAsArray(String tableFieldDefinition){
		List<String> fieldsNameList = getTableFieldNames(tableFieldDefinition);
		
		return fieldsNameList.toArray(new String[fieldsNameList.size()]);
	}
	
	public static HashMap<String, Class<?>> getTableFieldDefinitionMap(String tableFieldDefinition){
		HashMap<String, Class<?>> ret = new HashMap<>();
		for(String fieldDef: tableFieldDefinition.split(TABLE_FIELD_DEFINITION_DELIMITER)){
			String fieldDefSplit[] = fieldDef.split(FIELD_DEFINITION_DELIMITER);
			Class<?> type = ImpalaParser.convertImpalaTypeToJavaType(fieldDefSplit[1]);
			ret.put(fieldDefSplit[0], type);
		}
		
		return ret;
	}
	
	public static Class<?> convertImpalaTypeToJavaType(String impalaType){
		Class<?> ret = null;
		switch(impalaType){
		case "STRING": 
			ret = String.class;
			break;
		case "BIGINT":
			ret = Long.class;
			break;
		case "BOOLEAN":
			ret = Boolean.class;
			break;
		case "DOUBLE":
			ret = Double.class;
			break;
		case "FLOAT":
			ret = Float.class;
			break;
		case "INT":
			ret = Integer.class;
			break;
		case "SMALLINT":
			ret = Short.class;
			break;
		case "TIMESTAMP":
			ret = ImpalaDateTime.class;
			break;
		}
		
		return ret;
	}

}
