package fortscale.streaming.task.monitor;

/**
 * Created by shays on 05/11/2015.
 */
public class EventTimeRange {
    private long timeOfFirstEventInWindow;
    private String timeOfFirstEventInWindowAsString;
    private long timeOfLastEventInWindow;
    private String timeOfLastEventInWindowAsString;

    public EventTimeRange(){
        timeOfFirstEventInWindow = Long.MAX_VALUE;
        timeOfLastEventInWindow = Long.MIN_VALUE;
        timeOfFirstEventInWindowAsString= "";
        timeOfLastEventInWindowAsString = "";
    }
    public long getTimeOfFirstEventInWindow() {
        return timeOfFirstEventInWindow;
    }

    public void setTimeOfFirstEventInWindow(long timeOfFirstEventInWindow) {
        this.timeOfFirstEventInWindow = timeOfFirstEventInWindow;
    }

    public String getTimeOfFirstEventInWindowAsString() {
        return timeOfFirstEventInWindowAsString;
    }

    public void setTimeOfFirstEventInWindowAsString(String timeOfFirstEventInWindowAsString) {
        this.timeOfFirstEventInWindowAsString = timeOfFirstEventInWindowAsString;
    }

    public long getTimeOfLastEventInWindow() {
        return timeOfLastEventInWindow;
    }

    public void setTimeOfLastEventInWindow(long timeOfLastEventInWindow) {
        this.timeOfLastEventInWindow = timeOfLastEventInWindow;
    }

    public String getTimeOfLastEventInWindowAsString() {
        return timeOfLastEventInWindowAsString;
    }

    public void setTimeOfLastEventInWindowAsString(String timeOfLastEventInWindowAsString) {
        this.timeOfLastEventInWindowAsString = timeOfLastEventInWindowAsString;
    }
}
