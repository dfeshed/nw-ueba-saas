package fortscale.streaming.task.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shays on 23/11/2015.
 */
public class TaskMonitoringDTO {
    private Map<String, Integer> countFilterByCause;
    protected int countNotFilteredEvents;

    //Map which contains the first and last event of each data source
    EventTimeRange eventTimeRange;
    private int totalAmountOfEventsInWindow; //Filtered and unfiltered events

    public TaskMonitoringDTO(){
        countFilterByCause = new HashMap<>();
        countNotFilteredEvents = 0;
        totalAmountOfEventsInWindow = 0;
        eventTimeRange = new EventTimeRange();
    }

    public void increaseTotalEventsCount() {
        totalAmountOfEventsInWindow++;
    }

    public void addCause(String cause) {
        Integer causeCount = countFilterByCause.get(cause);
        if (causeCount == null){
            causeCount = 1;
        } else {
            causeCount++;
        }
        countFilterByCause.put(cause,causeCount);
    }

    //Keep the time of the first and last event time in the windows
    //First and last could be the same if there is only one event in the window
    public  void updateFirstLastEventInWindow(Long time, String dateAsString) {
        if (time == null || dateAsString == null) {
            return;
        }

        long eventTime = time.longValue();

        if (eventTime < eventTimeRange.getTimeOfFirstEventInWindow()) {
            eventTimeRange.setTimeOfFirstEventInWindow(eventTime);
            eventTimeRange.setTimeOfFirstEventInWindowAsString(dateAsString);
        }

        if (eventTime > eventTimeRange.getTimeOfLastEventInWindow()) {
            eventTimeRange.setTimeOfLastEventInWindow(eventTime);
            eventTimeRange.setTimeOfLastEventInWindowAsString(dateAsString);
        }
    }

    public void increaseNotFilteredEvents() {
        this.countNotFilteredEvents++;
    }


    public Map<String, Integer> getCountFilterByCause() {
        return countFilterByCause;
    }

    public void setCountFilterByCause(Map<String, Integer> countFilterByCause) {
        this.countFilterByCause = countFilterByCause;
    }

    public int getCountNotFilteredEvents() {
        return countNotFilteredEvents;
    }

    public void setCountNotFilteredEvents(int countNotFilteredEvents) {
        this.countNotFilteredEvents = countNotFilteredEvents;
    }

    public EventTimeRange getEventTimeRange() {
        return eventTimeRange;
    }

    public void setEventTimeRange(EventTimeRange eventTimeRange) {
        this.eventTimeRange = eventTimeRange;
    }

    public int getTotalAmountOfEventsInWindow() {
        return totalAmountOfEventsInWindow;
    }

    public void setTotalAmountOfEventsInWindow(int totalAmountOfEventsInWindow) {
        this.totalAmountOfEventsInWindow = totalAmountOfEventsInWindow;
    }
}
