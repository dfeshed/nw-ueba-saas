package fortscale.utils.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Time utility functions
 *
 * @author gils
 * Date: 05/08/2015
 */
public class TimeUtils {
    private static final DateFormat defaultTimeFormat;
    private static final DateFormat utcTimeFormat;

    static {
        defaultTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        utcTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'");
        utcTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static final int HOUR_LOWER_BOUND = 0;
    private static final int HOUR_UPPER_BOUND = 23;

    public static String getFormattedTime(Long timeInMillis) {
        Calendar calInstance = Calendar.getInstance();
        calInstance.setTimeInMillis(timeInMillis);
        return defaultTimeFormat.format(calInstance.getTime());
    }

    public static String getUtcFormat(Date date) {
        return utcTimeFormat.format(date);
    }

    public static String getUtcFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static Long calculateStartingTime(Long endTimeInMillis, int timePeriodInDays) {
        Calendar calEvidenceTime = Calendar.getInstance();
        calEvidenceTime.setTimeInMillis(endTimeInMillis);

        calEvidenceTime.add(Calendar.DAY_OF_MONTH, -1 * timePeriodInDays);

        return calEvidenceTime.getTimeInMillis();
    }

    public static int getOrdinalDayOfWeek(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getDayOfWeek(int ordinalDayOfWeek) {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (ordinalDayOfWeek == dayOfWeek.getDayValue()) {
                return dayOfWeek.name();
            }
        }

        return null;
    }

    public static boolean isOrdinalHourValid(Integer hour) {
        return hour > HOUR_UPPER_BOUND || hour < HOUR_LOWER_BOUND;
    }

    private enum DayOfWeek { // TODO exists in JDK 1.8..
        SUNDAY(1),
        MONDAY(2),
        TUESDAY(3),
        WEDNESDAY(4),
        THURSDAY(5),
        FRIDAY(6),
        SATURDAY(7);

        private int dayValue; // enum ordinal starts from 0 so we need to normalized the index to start from 1..

        DayOfWeek(int dayValue) {
            this.dayValue = dayValue;
        }

        public int getDayValue() {
            return dayValue;
        }
    }
}
