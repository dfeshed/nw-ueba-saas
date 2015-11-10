package fortscale.streaming.task.monitor;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shays on 05/11/2015.
 */

public class TaskMonitoringHelper {

    //Parameters for Window statistics monitoring
    @Autowired
    private JobProgressReporter jobMonitorReporter;
    private Map<String, Integer> countFilterByCause;
    protected int countNotFilteredEvents;

    //Map which contains the first and last event of each data source
    Map<String,EventTimeRange> eventTimeRange;
    private int totalAmountOfEventsInWindow; //Filtered and unfiltered events

    //Constant labels for JOB monitoring
    public static final String TOTAL_FILTERED_EVENTS_LABEL = "Filtered Events";
    public static final String FIRST_EVENT_TIME_LABEL = "First Event Original Time";
    public static final String LAST_EVENT_TIME_LABEL = "Last Event Original Time";
    public static final String TOTAL_EVENTS_LABEL = "Total Events";
    public static final String NOT_FILTERED_EVENTS_LABEL = "Processed Event";
    public static final String JOB_DATA_SOURCE = "Streaming";
    private static final String EVENTS_TYPE="EVENTS";
    private static final String FILTERED_EVENTS_PREFIX = "Filtered Events - Reason ";


    private boolean isMonitoredTask;

    public boolean isMonitoredTask() {
        return isMonitoredTask;
    }

    public void setIsMonitoredTask(boolean isMonitoredTask) {
        this.isMonitoredTask = isMonitoredTask;
    }


    /**
     * Called for each new event
     * doesn't matter if the event will be filtered or not
     */
    public void handleNewEvent(){
        if (isMonitoredTask()) {
            totalAmountOfEventsInWindow++;
        }
    }

    /**
     * When event filtered, call that method with the cause.
     * That method add the cause  countFilterByCause, if the cause already in the map,
     * the counter of the cause increased
     * @param cause
     */
    public void countNewFilteredEvents(String dataSource, String cause){
        String causeLabel="";
        if (StringUtils.isNotBlank(dataSource)){
            causeLabel = "Data Source: "+dataSource+". ";
        }
        causeLabel +=cause;

        Integer causeCount = countFilterByCause.get(causeLabel);
        if (causeCount == null){
            causeCount = 1;
        } else {
            causeCount++;
        }
        countFilterByCause.put(causeLabel,causeCount);
    }


    //Init all statistics per windows
    public void resetCountersPerWindow() {
        countFilterByCause = new HashMap<>();
        countNotFilteredEvents = 0;
        totalAmountOfEventsInWindow = 0;
        eventTimeRange = new HashMap<>();


    }

    public void handleUnFilteredEvents(String datasource, Number dateTimeUnix, String dateAsString){
        updateFirstLastEventInWindow(datasource,dateTimeUnix, dateAsString);
        countNotFilteredEvents++; //Count not filtered events per window
    }
    /**
     * Create new instance of job report, with the time of the first event in the window,
     * the time of last event of the window, the total number of events in window (filtered and not filtered)
     * the number of unfiltered events in the window,
     * and how many filtered events per each cause.
     * If there where no filtered event, add one line of Filter events = 0
     */
    public void saveJobStatusReport(String jobLabel){

        if (isMonitoredTask()) {
            String monitorId = jobMonitorReporter.startJob(JOB_DATA_SOURCE, jobLabel, 1, true);

            //All the events which arrive to the job in the windows
            addJobData(monitorId, TOTAL_EVENTS_LABEL, totalAmountOfEventsInWindow, EVENTS_TYPE);

            for (Map.Entry<String, EventTimeRange> firstLastEventTime : eventTimeRange.entrySet()) {
                String textPrefix = eventTimeRange.size() > 1 ? firstLastEventTime.getKey() +": " : "";
                //Original time of first event in the window
                addJobData(monitorId, textPrefix + FIRST_EVENT_TIME_LABEL, null, firstLastEventTime.getValue().getTimeOfFirstEventInWindowAsString());
                //Original time of last event in the window
                addJobData(monitorId, textPrefix+ LAST_EVENT_TIME_LABEL, null, firstLastEventTime.getValue().getTimeOfLastEventInWindowAsString());
            }

            //Add all cause and how many events filtered per cause,
            //or add "filtered events = 0 if no filtered events in the window.
            if (countFilterByCause.size() > 0) {
                for (Map.Entry<String, Integer> cause : countFilterByCause.entrySet()) {
                    String label = FILTERED_EVENTS_PREFIX + cause.getKey();
                    addJobData(monitorId, label, cause.getValue(), EVENTS_TYPE);
                }
            } else {
                addJobData(monitorId, TOTAL_FILTERED_EVENTS_LABEL, 0, EVENTS_TYPE);
            }

            //How many events not filtered in the window
            addJobData(monitorId, NOT_FILTERED_EVENTS_LABEL, countNotFilteredEvents, EVENTS_TYPE);

            jobMonitorReporter.finishJob(monitorId);

            //Reset counters per window
            resetCountersPerWindow();
        }
    }

    //Keep the time of the first and last event time in the windows
    //First and last could be the same if there is only one event in the window
    private void updateFirstLastEventInWindow(String dataSource, Number time, String dateAsString){
        if (time == null || dateAsString==null) {
            return;
        }
        long eventTime = time.longValue();

        EventTimeRange timeRange = eventTimeRange.get(dataSource);
        if (timeRange != null) {
            if (eventTime < timeRange.getTimeOfFirstEventInWindow()) {
                timeRange.setTimeOfFirstEventInWindow(eventTime);
                timeRange.setTimeOfFirstEventInWindowAsString(dateAsString);
            }

            if (eventTime > timeRange.getTimeOfLastEventInWindow()) {
                timeRange.setTimeOfLastEventInWindow(eventTime);
                timeRange.setTimeOfLastEventInWindowAsString(dateAsString);
            }
        } else {
            timeRange = new EventTimeRange();
            eventTimeRange.put(dataSource, timeRange);
        }

    }


    /**
     * Create new instance of JobDataReceived and add it to monitor
     * @param monitorId
     * @param text
     * @param value
     * @param valueType
     */
    private void addJobData(String monitorId, String text, Integer value, String valueType ){
        JobDataReceived dataReceived = new JobDataReceived(text, value, valueType);
        jobMonitorReporter.addDataReceived(monitorId,dataReceived);
    }
}
