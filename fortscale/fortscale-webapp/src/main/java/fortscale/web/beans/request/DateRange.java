package fortscale.web.beans.request;

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
}
