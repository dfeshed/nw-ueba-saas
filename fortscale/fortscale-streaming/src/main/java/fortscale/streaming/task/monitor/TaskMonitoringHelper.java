package fortscale.streaming.task.monitor;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.utils.spring.SpringPropertiesUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shays on 05/11/2015.
 */

public class TaskMonitoringHelper {

    //Parameters for Window statistics monitoring
    @Autowired
    private JobProgressReporter jobMonitorReporter;

    @Autowired
    private SpringPropertiesUtil messages;


    //Node is the task for specific data source and last state
    private Map<StreamingTaskDataSourceConfigKey, TaskMonitoringDTO> nodeMonitoringDetails = new HashMap<>();

    //Constant labels for JOB monitoring
    public static final String TOTAL_FILTERED_EVENTS_LABEL = "Filtered Events";
    public static final String FIRST_EVENT_TIME_LABEL = "First Event Original Time";
    public static final String LAST_EVENT_TIME_LABEL = "Last Event Original Time";
    public static final String TOTAL_EVENTS_LABEL = "Total Events";
    public static final String NOT_FILTERED_EVENTS_LABEL = "Processed Event";
    public static final String JOB_DATA_SOURCE = "Streaming";
    public  static final String EVENTS_TYPE="EVENTS";
    private static final String FILTERED_EVENTS_PREFIX = "Filtered Events - Reason ";


    private boolean isMonitoredTask;

    public boolean isMonitoredTask() {
        return isMonitoredTask;
    }

    public void setIsMonitoredTask(boolean isMonitoredTask) {
        this.isMonitoredTask = isMonitoredTask;
    }

    private TaskMonitoringDTO getNode(StreamingTaskDataSourceConfigKey key){
        TaskMonitoringDTO node = nodeMonitoringDetails.get(key);
        if (node == null){
            node = new TaskMonitoringDTO();
            nodeMonitoringDetails.put(key,node);
        }

        return node;
    }

    /**
     * Called for each new event
     * doesn't matter if the event will be filtered or not
     */
    public void handleNewEvent(StreamingTaskDataSourceConfigKey key){
        if (isMonitoredTask()) {
            TaskMonitoringDTO node = getNode(key);
            node.increaseTotalEventsCount();
        }
    }

    /**
     * When event filtered, call that method with the cause.
     * That method add the cause  countFilterByCause, if the cause already in the map,
     * the counter of the cause increased
     * @param cause
     */
    public void countNewFilteredEvents(StreamingTaskDataSourceConfigKey key, String cause, String... args){

        //Get the text from messages file.
        String text = null;
        if (messages != null) {
            text = messages.getProperty(cause);
        }
        //If not exists in messages file use the original cause
        if (StringUtils.isBlank(text)){
            text = cause;
        }
        if (args!= null && args.length>0){
            text = String.format(text,args);
        }
        TaskMonitoringDTO node = getNode(key);
        node.increaseCauseCount(text);

    }


    //Init all statistics per windows
    public void resetCountersPerWindow() {
        nodeMonitoringDetails.clear();
    }

    public void handleUnFilteredEvents(StreamingTaskDataSourceConfigKey key, Long dateTimeUnix, String dateAsString){

        TaskMonitoringDTO node = getNode(key);

        node.updateFirstLastEventInWindow(dateTimeUnix, dateAsString);
        node.increaseNotFilteredEvents(); //Count not filtered events per window
    }
    /**
     * Create new instance of job report, with the time of the first event in the window,
     * the time of last event of the window, the total number of events in window (filtered and not filtered)
     * the number of unfiltered events in the window,
     * and how many filtered events per each cause.
     * If there where no filtered event, add one line of Filter events = 0
     */
    public void saveJobStatusReport(String jobLabel,boolean saveOnlyIfDataExists){

        //If tasks is not monitored - stop saving and do nothing
        if (!isMonitoredTask()) {
            return;
        }

        //If there were no events in the window and saveOnlyIfDataExists turned on,
        //stop saving and do nothing
        if (saveOnlyIfDataExists && MapUtils.isEmpty(this.nodeMonitoringDetails)){
            return;
        }

        //Start saving:
        String monitorId = jobMonitorReporter.startJob(JOB_DATA_SOURCE, jobLabel, 1, true);

        for (Map.Entry<StreamingTaskDataSourceConfigKey,TaskMonitoringDTO> node: this.nodeMonitoringDetails.entrySet()){
            StreamingTaskDataSourceConfigKey nodeKey = node.getKey();
            String nodePrefix = "";
            if (nodeKey != null) {
                nodePrefix = node.getKey().getDataSource() + "/" + node.getKey().getLastState() + "- ";
            };

            TaskMonitoringDTO nodeData = node.getValue();

            addJobData(monitorId, nodePrefix+TOTAL_EVENTS_LABEL, nodeData.getTotalAmountOfEventsInWindow(), EVENTS_TYPE);
            addJobData(monitorId, nodePrefix + FIRST_EVENT_TIME_LABEL, null, nodeData.getEventTimeRange().getTimeOfFirstEventInWindowAsString());
            addJobData(monitorId, nodePrefix + LAST_EVENT_TIME_LABEL, null,  nodeData.getEventTimeRange().getTimeOfLastEventInWindowAsString());

            //Add all cause and how many events filtered per cause,
            //or add "filtered events = 0 if no filtered events in the window.
            if (nodeData.getCountFilterByCause().size() > 0) {
                for (Map.Entry<String, Integer> cause : nodeData.getCountFilterByCause().entrySet()) {
                    String label = nodePrefix + FILTERED_EVENTS_PREFIX + cause.getKey();
                    addJobData(monitorId, label, cause.getValue(), EVENTS_TYPE);
                }
            } else {
                addJobData(monitorId, nodePrefix + TOTAL_FILTERED_EVENTS_LABEL, 0, EVENTS_TYPE);

            }
        //How many events not filtered in the window
        addJobData(monitorId, nodePrefix + NOT_FILTERED_EVENTS_LABEL, nodeData.getCountNotFilteredEvents(), EVENTS_TYPE);


        }


        jobMonitorReporter.finishJob(monitorId);

        //Reset counters per window
        resetCountersPerWindow();

    }




    /**
     * Create new instance of JobDataReceived and add it to monitor
     * @param monitorId
     * @param text
     * @param value
     * @param valueType
     */
    private void addJobData(String monitorId, String text, Integer value, String valueType ){

		JobDataReceived dataReceived = new JobDataReceived(text,value, valueType);

        jobMonitorReporter.addDataReceived(monitorId,dataReceived);
    }
}
