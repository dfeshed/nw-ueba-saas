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
	public static final String TOTAL_FILTERED_EVENTS_LABEL = "Filtered Events";
	public static final String FIRST_EVENT_TIME_LABEL = "First Event Time";
	public static final String LAST_EVENT_TIME_LABEL = "Last Event Time";
	public static final String NOT_FILTERED_EVENTS_LABEL = "Not Filtered Events";
	public static final String JOB_DATA_SOURCE = "Streaming";
	public static final String MONITOR_NAME_POSTFIX = "EventsFilterStreaming";
	private static final String EVENTS_TYPE="EVENTS";
	private static final String FILTERED_EVENTS_PREFIX = "Filtered Events - ";

	private String outputTopic;
	private String dataSource;
	private Counter processedFilterCount;
	private Counter processedNonFilterCount;

	private JobProgressReporter jobMonitorReporter;
	private Map<String, Integer> countFilterByCause;
	private int countNotFilteredEvents;


	private long timeOfFirstEventInWindow;
	private String timeOfFirstEventInWindowAsString;
	private long timeOfLastEventInWindow;
	private String timeOfLastEventInWindowAsString;
	
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

	private void initCountersPerWindow() {
		countFilterByCause = new HashMap<>();
		countNotFilteredEvents = 0;
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
		countNotFilteredEvents++; //This parameter initilized for each new window
		processedNonFilterCount.inc();
		
	}

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
	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		saveJobStatusReport();

	}

	private void saveJobStatusReport() {
		String monitorId =  jobMonitorReporter.startJob(JOB_DATA_SOURCE,getSpecificDataSource()+ MONITOR_NAME_POSTFIX,1,true);

		addJobData(monitorId, FIRST_EVENT_TIME_LABEL,null, timeOfFirstEventInWindowAsString);
		addJobData(monitorId, LAST_EVENT_TIME_LABEL,null, timeOfLastEventInWindowAsString);
		addJobData(monitorId, NOT_FILTERED_EVENTS_LABEL,countNotFilteredEvents, EVENTS_TYPE);

		if (countFilterByCause.size() > 0) {
			for (Map.Entry<String, Integer> cause : countFilterByCause.entrySet()) {
				String label = FILTERED_EVENTS_PREFIX+ cause.getKey();
				addJobData(monitorId, label, cause.getValue(), EVENTS_TYPE);
			}
		} else {
			addJobData(monitorId, TOTAL_FILTERED_EVENTS_LABEL, 0, EVENTS_TYPE);
		}


		jobMonitorReporter.finishJob(monitorId);

		//Reset counters
		initCountersPerWindow();
	}

	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {

	}

	private void addJobData(String monitorId, String text, Integer value, String valueType ){
		JobDataReceived dataRecieved = new JobDataReceived(text, value, valueType);
		jobMonitorReporter.addDataReceived(monitorId,dataRecieved);
	}
	/** Auxiliary method to enable filtering messages on specific events types */
	protected boolean acceptMessage(JSONObject message){ return true;}

	protected void countNewFilteredEvents(String cause){
		Integer causeReason = countFilterByCause.get(cause);
		if (causeReason == null){
			causeReason = 1;
		} else {
			causeReason++;
		}
		countFilterByCause.put(cause,causeReason);
	}


	protected String getSpecificDataSource(){
		return "";
	}
}
