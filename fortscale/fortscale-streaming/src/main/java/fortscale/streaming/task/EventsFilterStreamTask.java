package fortscale.streaming.task;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import java.util.HashMap;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;


public class EventsFilterStreamTask extends AbstractStreamTask{
	//Constant labels for JOB monitoring
	public static final String TOTAL_FILTERED_EVENTS_LABEL = "Filtered Events";
	public static final String FIRST_EVENT_TIME_LABEL = "First Event Original Time";
	public static final String LAST_EVENT_TIME_LABEL = "Last Event Original Time";
	public static final String TOTAL_EVENTS_LABEL = "Total Events";
	public static final String NOT_FILTERED_EVENTS_LABEL = "Processed Event";
	public static final String JOB_DATA_SOURCE = "Streaming";
	public static final String MONITOR_NAME_POSTFIX = "EventsFilterStreaming";
	private static final String EVENTS_TYPE="EVENTS";
	private static final String FILTERED_EVENTS_PREFIX = "Filtered Events - Reason ";

	private String outputTopic;
	private String dataSource;
	private Counter processedFilterCount;
	private Counter processedNonFilterCount;

	//Parameters for Window statistics monitoring
	private JobProgressReporter jobMonitorReporter;
	private Map<String, Integer> countFilterByCause;
	private int countNotFilteredEvents;
	private long timeOfFirstEventInWindow;
	private String timeOfFirstEventInWindowAsString;
	private long timeOfLastEventInWindow;
	private String timeOfLastEventInWindowAsString;
	private int totalAmountOfEventsInWindow; //Filtered and unfiltered events
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		outputTopic = config.get("fortscale.output.topic", "");
		dataSource = getConfigString(config, "fortscale.data.source");
		// create counter metric for processed messages
		processedFilterCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-event-filter-count", dataSource));
		processedNonFilterCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-event-non-filter-count", dataSource));

		jobMonitorReporter = SpringService.getInstance().resolve(JobProgressReporter.class);
		initCountersPerWindow();

	}

	//Init all statistics per windows
	private void initCountersPerWindow() {
		countFilterByCause = new HashMap<>();
		countNotFilteredEvents = 0;
		totalAmountOfEventsInWindow = 0;
		timeOfFirstEventInWindow = Long.MAX_VALUE;
		timeOfLastEventInWindow = Long.MIN_VALUE;
		timeOfFirstEventInWindowAsString= "";
		timeOfLastEventInWindowAsString = "";
	}

	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		totalAmountOfEventsInWindow++;

		if (!acceptMessage(message)) {
			processedFilterCount.inc();
			return;
		}
		
		// publish the event with score to the subsequent topic in the topology
		if (StringUtils.isNotEmpty(outputTopic)){
			try{
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
			} catch(Exception exception){
				throw new KafkaPublisherException(String.format("failed to send scoring message after processing the message %s.", messageText), exception);
			}
		}

		updateFirstLastEventInWindow(message.getAsNumber("date_time_unix"), message.getAsString("date_time"));
		countNotFilteredEvents++; //Count not filtered events per window
		processedNonFilterCount.inc(); //Count not filtered events total
		
	}


	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		saveJobStatusReport();
	}


	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {
		saveJobStatusReport();
	}


	/** Auxiliary method to enable filtering messages on specific events types */
	protected boolean acceptMessage(JSONObject message){ return true;}

	/**
	 * When event filtered, call that method with the cause.
	 * That method add the cause  countFilterByCause, if the cause already in the map,
	 * the counter of the cause increased
	 * @param cause
	 */
	protected void countNewFilteredEvents(String cause){
		Integer causeReason = countFilterByCause.get(cause);
		if (causeReason == null){
			causeReason = 1;
		} else {
			causeReason++;
		}
		countFilterByCause.put(cause,causeReason);
	}


	/**
	 * Abstract method to get the prefix of the job name, depnded on the class
	 * @return
	 */
	protected String getSpecificDataSource(){
		return "";
	}


	/**
	 * Create new instance of job report, with the time of the first event in the window,
	 * the time of last event of the window, the total number of events in window (filtered and not filtered)
	 * the number of unfiltered events in the window,
	 * and how many filtered events per each cause.
	 * If there where no filtered event, add one line of Filter events = 0
	 */
	private void saveJobStatusReport() {
		String monitorId =  jobMonitorReporter.startJob(JOB_DATA_SOURCE,getSpecificDataSource()+ MONITOR_NAME_POSTFIX,1,true);

		//All the events which arrive to the job in the windows
		addJobData(monitorId, TOTAL_EVENTS_LABEL,totalAmountOfEventsInWindow, EVENTS_TYPE);
		//Original time of first event in the window
		addJobData(monitorId, FIRST_EVENT_TIME_LABEL,null, timeOfFirstEventInWindowAsString);
		//Original time of last event in the window
		addJobData(monitorId, LAST_EVENT_TIME_LABEL,null, timeOfLastEventInWindowAsString);

		//Add all cause and how many events filtered per cause,
		//or add "filtered events = 0 if no filtered events in the window.
		if (countFilterByCause.size() > 0) {
			for (Map.Entry<String, Integer> cause : countFilterByCause.entrySet()) {
				String label = FILTERED_EVENTS_PREFIX+ cause.getKey();
				addJobData(monitorId, label, cause.getValue(), EVENTS_TYPE);
			}
		} else {
			addJobData(monitorId, TOTAL_FILTERED_EVENTS_LABEL, 0, EVENTS_TYPE);
		}

		//How many events not filtered in the window
		addJobData(monitorId, NOT_FILTERED_EVENTS_LABEL,countNotFilteredEvents, EVENTS_TYPE);

		jobMonitorReporter.finishJob(monitorId);

		//Reset counters per window
		initCountersPerWindow();
	}

	//Keep the time of the first and last event time in the windows
	//First and last could be the same if there is only one event in the window
	private void updateFirstLastEventInWindow(Number time, String dateAsString){
		long eventTime = time.longValue();
		if (eventTime < timeOfFirstEventInWindow){
			timeOfFirstEventInWindow = eventTime;
			timeOfFirstEventInWindowAsString = dateAsString;
		}

		if (eventTime > timeOfLastEventInWindow){
			timeOfLastEventInWindow = eventTime;
			timeOfLastEventInWindowAsString = dateAsString;
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
