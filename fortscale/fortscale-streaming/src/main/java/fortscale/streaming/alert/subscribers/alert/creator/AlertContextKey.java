package fortscale.streaming.alert.subscribers.alert.creator;

/**
 * Created by shays on 27/03/2016.
 */
public class AlertContextKey {

    private long startTime;
    private long endTime;
    private String alertName;

    public AlertContextKey(String alertName, long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.alertName = alertName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertContextKey)) return false;

        AlertContextKey that = (AlertContextKey) o;

        if (startTime != that.startTime) return false;
        if (endTime != that.endTime) return false;
        return alertName.equals(that.alertName);

    }

    @Override
    public int hashCode() {
        int result = (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        result = 31 * result + alertName.hashCode();
        return result;
    }
}
