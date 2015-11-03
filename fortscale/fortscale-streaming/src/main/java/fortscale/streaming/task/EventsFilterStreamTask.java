package fortscale.streaming.task;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.utils.time.TimeUtils;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;


public class EventsFilterStreamTask extends AbstractStreamTask{

//	private static final Logger logger = LoggerFactory.getLogger(EventsFilterStreamTask.class);
	
	private String outputTopic;
	private String dataSource;
	private Counter processedFilterCount;
	private Counter processedNonFilterCount;

	private JobProgressReporter jobMonitorReporter;
	private Map<String, Integer> countFilterByCause;
	private int countNotFilteredEvents;
	private static final String EVENTS_TYPE="EVENTS";
	private long timeOfFirstEventInWindow;
	private long timeOfLastEventInWindow;
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		outputTopic = config.get("fortscale.output.topic", "");
		dataSource = getConfigString(config, "fortscale.data.source");
		// create counter metric for processed messages
		processedFilterCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-event-filter-count", dataSource));
		processedNonFilterCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-event-non-filter-count", dataSource));

		jobMonitorReporter = SpringService.getInstance().resolve(JobProgressReporter.class);
		countFilterByCause = new HashMap<>();
		countNotFilteredEvents = 0;
		timeOfFirstEventInWindow = Long.MAX_VALUE;
		timeOfLastEventInWindow = Long.MIN_VALUE;

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

		updateFirstLastEventInWindow(message.getAsNumber("date_time_unix"));
		countNotFilteredEvents++; //This parameter initilized for each new window
		processedNonFilterCount.inc();
		
	}

	private void updateFirstLastEventInWindow(Number time){
		long eventTime = time.longValue();
		if (eventTime < timeOfFirstEventInWindow){
			timeOfFirstEventInWindow = eventTime;
		}

		if (eventTime > timeOfLastEventInWindow){
			timeOfLastEventInWindow = eventTime;
		}


	}
	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		saveJobStatusReport();

	}

	private void saveJobStatusReport() {
		String monitorId =  jobMonitorReporter.startJob(dataSource,"EventsFilterStreaming",1,true);





		addJobData(monitorId,"First Event Time",1, timeOfFirstEventInWindow+"");
		addJobData(monitorId,"Last Event Time",1, timeOfLastEventInWindow+"");
		addJobData(monitorId,"Not Filter Events",countNotFilteredEvents, EVENTS_TYPE);

		for (Map.Entry<String, Integer> cause: countFilterByCause.entrySet()){
			addJobData(monitorId,cause.getKey(), cause.getValue(), EVENTS_TYPE);
		}


		jobMonitorReporter.finishJob(monitorId);

		//Reset counters
		timeOfFirstEventInWindow = Long.MAX_VALUE;
		timeOfLastEventInWindow = Long.MIN_VALUE;
		countNotFilteredEvents = 0;
		countFilterByCause.clear();
	}

	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {

	}

	private void addJobData(String monitorId, String text, int value, String valueType ){
		JobDataReceived dataRecieved = new JobDataReceived(text, value, valueType);
		jobMonitorReporter.addDataReceived(monitorId,dataRecieved);
	}
	/** Auxiliary method to enable filtering messages on specific events types */
	protected boolean acceptMessage(JSONObject message){ return true;}

	protected void countNewFilteredEvents(String cause){
		Integer causeReason = countFilterByCause.get(cause);
		if (causeReason == null){
			countFilterByCause.put(cause,1);
		} else {
			causeReason++;
		}
	}
}
