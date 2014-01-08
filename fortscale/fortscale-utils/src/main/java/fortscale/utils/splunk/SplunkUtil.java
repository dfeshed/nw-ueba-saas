package fortscale.utils.splunk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fortscale.utils.logging.Logger;

public class SplunkUtil {
	private static final Logger logger = Logger.getLogger(SplunkUtil.class);
	
	public static int compareCursors(String cursor1, String cursor2){
		int ret = 0;
		try{
			Date cursorDate1 = getCursorDate(cursor1);
			Date cursorDate2 = getCursorDate(cursor2);
			ret = cursorDate1.compareTo(cursorDate2);
		} catch(ParseException e){
			logger.error("got an exception while trying to parse the cursors", e);
		}
		
		return ret;
	}
	
	public static Date getCursorDate(String cursor) throws ParseException{
		SimpleDateFormat f = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss");
		String time = cursor.substring(0,cursor.indexOf('.'));
		Date d = f.parse(time);
		
		return d;
	}
	
	public static String cursorToSearchTimeQueryFormat(String time) throws ParseException{
		StringBuffer ret = new StringBuffer();
		String splitTime[] = time.split("T");
		String splitDate[] = splitTime[0].split("-");
		String dayTimeSplit[] = splitTime[1].substring(0, splitTime[1].indexOf(".")).split(":");
		
		String year = splitDate[0];
		String day = getShortTimeUnit(splitDate[2]);
		String month = getShortTimeUnit(splitDate[1]);
		
		String hour = getShortTimeUnit(dayTimeSplit[0]);
		String minute = getShortTimeUnit(dayTimeSplit[1]);
		String second = getShortTimeUnit(dayTimeSplit[2]);
		
		ret.append(month).append("/").append(day).append("/").append(year).append(":").append(hour).append(":").append(minute).append(":").append(second);
		
		return ret.toString();
	}
	
	private static String getShortTimeUnit(String fullTimeUnit){
		if(fullTimeUnit.startsWith("0")){
			fullTimeUnit = fullTimeUnit.substring(1, 2);
		}
		return fullTimeUnit;
	}
}
