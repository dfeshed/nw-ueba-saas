package fortscale.utils.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author gils
 * Date: 05/08/2015
 */
public class TimeUtils {

    private static final String UTC_TIMEZONE = "UTC";
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static String getFormattedTime(Long timeInMillis) {
        Calendar calInstance = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));
        calInstance.setTimeInMillis(timeInMillis);

        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(calInstance.getTime());
    }

    public static Long calculateStartingTime(Long endTimeInMillis, int timePeriodInDays) {
        Calendar calEvidenceTime = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));
        calEvidenceTime.setTimeInMillis(endTimeInMillis);

        calEvidenceTime.add(Calendar.DAY_OF_MONTH, -1 * timePeriodInDays);

        return calEvidenceTime.getTimeInMillis();
    }

    public static int getDayOfWeek(long timeInMillis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}
