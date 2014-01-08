package fortscale.utils.impala;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImpalaParser {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	public Date parseTimeDate(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
		return pattern.parse(dateString);
	}
	
	public String formatTimeDate(Date date) {
		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
		return pattern.format(date);
	}
	
	public long getRuntime(Date timestamp){
		return timestamp.getTime()/1000;
	}
}
