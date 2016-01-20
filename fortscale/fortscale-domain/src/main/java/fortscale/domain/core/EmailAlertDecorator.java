package fortscale.domain.core;

import fortscale.domain.core.Alert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by avivs on 20/01/16.
 */
public class EmailAlertDecorator extends Alert {

    private static final String SHORT_DATE_FORMAT = "EEE, MM/dd/yy";
    private static final String LONG_DATE_FORMAT = "MM/dd/yy HH:mm:ss";

    private String startDateShort;
    private String startDateLong;

    private String endDateShort;
    private String endDateLong;

    public EmailAlertDecorator() {}

    public EmailAlertDecorator(Alert alert) {
        super(alert);

        // Decorate dates
        this.startDateShort = formatDate(alert.getStartDate(), SHORT_DATE_FORMAT);
        this.startDateLong = formatDate(alert.getStartDate(), LONG_DATE_FORMAT);
        this.endDateShort = formatDate(alert.getStartDate(), SHORT_DATE_FORMAT);
        this.endDateLong = formatDate(alert.getStartDate(), LONG_DATE_FORMAT);
    }


    /**
     * Returns a formatted date/time string
     *
     * @param date An epoch time date
     * @param dateFormat String representing the desired time format
     * @return Time/Date formatted string
     */
    private String formatDate (long date, String dateFormat) {
        Date oDate = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(tz);
        return sdf.format(oDate);
    }


    public String getStartDateShort() {
        return startDateShort;
    }

    public String getStartDateLong() {
        return startDateLong;
    }

    public String getEndDateShort() {
        return endDateShort;
    }

    public String getEndDateLong() {
        return endDateLong;
    }


}
