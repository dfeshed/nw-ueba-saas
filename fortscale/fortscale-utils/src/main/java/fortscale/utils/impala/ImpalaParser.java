package fortscale.utils.impala;

import java.text.ParseException;
import java.util.Date;

import org.joda.time.DateTime;

public class ImpalaParser {
	
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
