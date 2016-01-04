package fortscale.collection.morphlines;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by rans on 04/01/15.
 */
public class TestUtils {
    static TimeZone tz;
    static SimpleDateFormat sdf;

    static Calendar calendar = Calendar.getInstance();
    static Integer currentYear;
    static String year;

    public static void init(String dateFormat, String timezone){
        tz = TimeZone.getTimeZone(timezone);
        calendar.setTimeZone(tz);
        currentYear = calendar.get(Calendar.YEAR);
        year = Integer.toString(currentYear);
        sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        sdf.setTimeZone(tz);
    }
    @SuppressWarnings("deprecation") public static Date constuctDate(String inDate){
        try {
            Date parsedDate = sdf.parse(year + " " + inDate);
            Date currentDate = calendar.getTime();
            if (parsedDate.compareTo(currentDate)>0) {
                parsedDate.setYear(parsedDate.getYear() - 1);
            }
            return parsedDate;

        } catch (ParseException e) {
            throw new RuntimeException(e);

        }
    }

    public static String getOutputDate(Date date){
        return getOutputDate(date, "yyyy-MM-dd HH:mm:ss");
    }
    public static String getOutputDate(Date date, String dateFormat){

        DateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        formatter.setCalendar(cal);
        return formatter.format(date);
    }

    public static Long getUnixDate(Date date){
        return date.getTime() / 1000;
    }
}
