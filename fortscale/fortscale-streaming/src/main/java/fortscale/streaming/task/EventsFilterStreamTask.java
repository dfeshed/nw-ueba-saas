package fortscale.streaming.task;

import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.task.metrics.EventsFilterStreamTaskMetrics;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import static fortscale.streaming.ConfigUtils.getConfigString;


public class EventsFilterStreamTask extends AbstractStreamTask {

	private String outputTopic;
	private Counter processedFilterCount;
	private Counter processedNonFilterCount;

	private static final String MONITOR_NAME = "EventsFilterStreaming";

	protected EventsFilterStreamTaskMetrics taskMetrics;

	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		outputTopic = config.get("fortscale.output.topic", "");
		String dataSource = getConfigString(config, "fortscale.data.source");
		// create counter metric for processed messages
		processedFilterCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-event-filter-count", dataSource));
		processedNonFilterCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-event-non-filter-count", dataSource));

	}



	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json
		JSONObject message = parseJsonMessage(envelope);


		if (!acceptMessage(message)) {
			++taskMetrics.filteredEvents;
			processedFilterCount.inc();
			return;
		}
		
		// publish the event with score to the subsequent topic in the topology
		if (StringUtils.isNotEmpty(outputTopic)){
			try{
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
				++taskMetrics.sentMessages;
			} catch(Exception exception){
				++taskMetrics.sendMessageFailures;
				throw new KafkaPublisherException(String.format("failed to send scoring message after processing the message %s.", (String)envelope.getMessage()), exception);
			}
		}

		if (taskMonitoringHelper.isMonitoredTask()) {
			handleUnfilteredEvent(message, extractDataSourceConfigKey(message));
		}
		++taskMetrics.unfilteredEvents;
		processedNonFilterCount.inc(); //Count not filtered events total
		
	}

	/**
	 * Create the task's specific metrics.
	 *
	 * Typically, the function is called from AbstractStreamTask.createTaskMetrics() at init()
	 */
	@Override
	protected void wrappedCreateTaskMetrics() {
		// Create the task's specific metrics
		taskMetrics = new EventsFilterStreamTaskMetrics(statsService);
	}


	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {

	}


	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {

	}

	/** Auxiliary method to enable filtering messages on specific events types */
	protected boolean acceptMessage(JSONObject message){ return true;}

	/**
	 * Abstract method to get the prefix of the job name, depnded on the class
	 * @return
	 */
	protected String getJobLabel(){
		return MONITOR_NAME;
	}

}
