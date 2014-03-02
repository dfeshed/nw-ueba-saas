package fortscale.utils.impala;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

public class ImpalaDateTime {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	
	private DateTime dateTime;
	private String toString;
	
	public ImpalaDateTime(DateTime dateTime){
		this.dateTime = dateTime;
		this.toString = formatTimeDate(dateTime);
	}
	
	public DateTime getDateTime(){
		return dateTime;
	}
	
	@Override
	public String toString(){
		return toString;
	}
	
	public static Date parseTimeDate(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
		return pattern.parse(dateString);
	}
	
	public static String formatTimeDate(Date date) {
		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
		return pattern.format(date);
	}
	
	public static DateTime parseTimeDateToDateTime(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
		return new DateTime(pattern.parse(dateString).getTime());
	}
	
	public static String formatTimeDate(DateTime dateTime) {
		return dateTime.toString(DATE_FORMAT);
	}
}
