package fortscale.domain.dto;

import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by shays on 23/05/2016.
 * Input class for filter on date by range of dates
 */
public class DateRange {

    private long fromTime;
    private long toTime;

    public DateRange() {
    }

    public DateRange(long fromTime, long toTime) {
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    /**
     *
     * @param dateRange
     * @return list of all the days (at 00:00:00) of each of the dayS between the start time and the end time
     */
    public List<Long> getDaysInRange(DateRange dateRange){
        List<Long> days = new ArrayList<>();

        long startOfFromDay = TimestampUtils.toStartOfDay(dateRange.getFromTime());
        long startOfEndDay = TimestampUtils.toStartOfDay(dateRange.getToTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(startOfFromDay));

        while (calendar.getTime().getTime() <=startOfEndDay){
            days.add(calendar.getTime().getTime());
            calendar.add(Calendar.DATE,1); //Add a day
        }

        return  days;

    }
}
