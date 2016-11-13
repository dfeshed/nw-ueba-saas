package fortscale.streaming.task;

import fortscale.services.impl.HdfsService;
import fortscale.services.impl.SpringService;
import fortscale.streaming.UserTimeBarrier;
import fortscale.streaming.exceptions.EnhancedExceptionHandler;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.exceptions.TaskCoordinatorException;
import fortscale.streaming.feature.extractor.FeatureExtractionService;
import fortscale.streaming.filters.MessageFilter;
import fortscale.streaming.service.BDPService;
import fortscale.streaming.service.BarrierService;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.task.metrics.HDFSWriterStreamingTaskMetrics;
import fortscale.streaming.task.metrics.HDFSWriterStreamingTaskTableWriterMetrics;
import fortscale.streaming.task.monitor.MonitorMessaages;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.apache.samza.task.TaskCoordinator.RequestScope;

import java.util.*;

import static fortscale.streaming.ConfigUtils.*;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Stream tasks that receives events and write them to hdfs using a partitioned
 * writer
 */
public class HDFSWriterStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {

	private static final String storeNamePrefix = "hdfs-write-";



	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(HDFSWriterStreamTask.class);

	/**
	 * Map from input topic to all relevant HDFS writes (can be more than 1, for example: for regular and "top" tables)
	 */
	protected Map<StreamingTaskDataSourceConfigKey, List<WriterConfiguration>> dataSourceToConfigsMap = new HashMap<>();

	private BDPService bdpService;

	// Streaming task metrics. Note some fields are update by this class and some by the derived classes
	protected HDFSWriterStreamingTaskMetrics taskMetrics;

	// Used to handle exceptions caught while trying to execute write operations
	private EnhancedExceptionHandler enhancedExceptionHandler;

    /** reads task configuration from job config and initialize hdfs appender */
	@SuppressWarnings("unchecked")
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {


		long windowDuration = config.getLong("task.window.ms");


		// Get configuration properties
		Config fieldsSubset = config.subset("fortscale.events.entry.name.");

		for (String dsSettings : fieldsSubset.keySet()) {

			// create specific configuration for data source and last state
			WriterConfiguration writerConfiguration = new WriterConfiguration();
			String datasource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", dsSettings));
			String lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", dsSettings));
			StreamingTaskDataSourceConfigKey configKey = new StreamingTaskDataSourceConfigKey(datasource, lastState);

			if (!dataSourceToConfigsMap.containsKey(configKey)) {
				dataSourceToConfigsMap.put(configKey, new ArrayList<>());
			}
			dataSourceToConfigsMap.get(configKey).add(writerConfiguration);

			if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.output.topics", dsSettings))) {
				writerConfiguration.outputTopics = getConfigStringList(config,String.format("fortscale.events.entry.%s.output.topics", dsSettings));
			}
			if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.bdp.output.topics", dsSettings))) {
				writerConfiguration.bdpOutputTopics = getConfigStringList(config,
						String.format("fortscale.events.entry.%s.bdp.output.topics", dsSettings));
			}

			// read configuration properties
			writerConfiguration.timestampField = resolveStringValue(config, String.format("fortscale.events.entry.%s.timestamp.field", dsSettings), res);
			writerConfiguration.usernameField = resolveStringValue(config, String.format("fortscale.events.entry.%s.username.field", dsSettings), res);
			List<String> discriminatorsFields = resolveStringValues(config, String.format("fortscale.events.entry.%s.discriminator.fields", dsSettings), res);
			writerConfiguration.fields = ImpalaParser.getTableFieldNames(resolveStringValue(config, String.format("fortscale.events.entry.%s.fields", dsSettings), res));
			writerConfiguration.separator = resolveStringValueDefault(config, String.format("fortscale.events.entry.%s.separator", dsSettings), ",", res);
			String hdfsRootPath = resolveStringValue(config, String.format("fortscale.events.entry.%s.hdfs.root", dsSettings), res);
			writerConfiguration.tableName = resolveStringValue(config, String.format("fortscale.events.entry.%s.table.name", dsSettings), res);
			String fileName = resolveStringValue(config, String.format("fortscale.events.entry.%s.file.name", dsSettings), res);
			writerConfiguration.partitionStrategy = PartitionsUtils.getPartitionStrategy(resolveStringValue(config, String.format("fortscale.events.entry.%s.partition.strategy", dsSettings), res));
			String splitClassName = resolveStringValue(config, String.format("fortscale.events.entry.%s.split.strategy", dsSettings), res);
			int eventsCountFlushThreshold = config.getInt(String.format("fortscale.events.entry.%s.events.flush.threshold", dsSettings));


			writerConfiguration.storeName = storeNamePrefix + writerConfiguration.tableName;
			FileSplitStrategy splitStrategy = (FileSplitStrategy) Class.forName(splitClassName).newInstance();

			// create HDFS appender service
			writerConfiguration.service = new HdfsService(hdfsRootPath, fileName, writerConfiguration.partitionStrategy,
					splitStrategy, writerConfiguration.tableName, eventsCountFlushThreshold, windowDuration, writerConfiguration.separator);
			writerConfiguration.featureExtractionService = new FeatureExtractionService(config, String.format("fortscale.events.entry.%s.feature.extractor.", dsSettings));

            // Create stats monitoring metrics for the writer
            writerConfiguration.tableWriterMetrics = new HDFSWriterStreamingTaskTableWriterMetrics(statsService,
					                                         configKey, writerConfiguration.tableName);

			// create counter metric for processed messages
			writerConfiguration.processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-write-count", writerConfiguration.tableName));
			writerConfiguration.skippedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-skip-count", writerConfiguration.tableName));
			writerConfiguration.lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-epochime", writerConfiguration.tableName));

			// get write time stamp barrier store
			writerConfiguration.barrier = new BarrierService((KeyValueStore<String, UserTimeBarrier>) context.getStore(writerConfiguration.storeName), discriminatorsFields);

			// load filters from configuration
			for (String filterName : config.getList(String.format("fortscale.events.entry.%s.filters", dsSettings), new LinkedList<>())) {
				MessageFilter filter = SpringService.getInstance().resolve(filterName, MessageFilter.class);
				filter.setName(filterName);
				writerConfiguration.filters.add(filter);
			}

			logger.info(String.format("Finished loading configuration for table %s (topic: %s) ", writerConfiguration.tableName, configKey));

		}
		bdpService = new BDPService();

		boolean enabled = resolveBooleanValue(config, "fortscale.enhanced.exception.handler.enabled", res);
		int numOfExceptionsToIgnore = config.getInt("fortscale.enhanced.exception.handler.num.of.exceptions.to.ignore");
		long sleepMillisBeforeRetry = config.getLong("fortscale.enhanced.exception.handler.sleep.millis.before.retry");
		int numOfAllowedExceptions = config.getInt("fortscale.enhanced.exception.handler.num.of.allowed.exceptions");
		enhancedExceptionHandler = new EnhancedExceptionHandler(enabled, numOfExceptionsToIgnore, sleepMillisBeforeRetry, numOfAllowedExceptions);
	}



	private List<String> resolveStringValues(Config config, String string, FortscaleValueResolver resolver) {
		return resolver.resolveStringValues(getConfigStringList(config, string));
	}

	private String resolveStringValueDefault(Config config, String string, String def, FortscaleValueResolver resolver) {
		return resolver.resolveStringValue(config.get(string, def));
	}

	/** Write the incoming message fields to hdfs */
	@Override
	public void wrappedProcess(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		// parse the message into json

		JSONObject message = parseJsonMessage(envelope);

		StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
		if (configKey == null){
			taskMetrics.unknownDataSourceEventMessages++;
			taskMonitoringHelper.countNewFilteredEvents(AbstractStreamTask.UNKNOW_CONFIG_KEY, MonitorMessaages.CANNOT_EXTRACT_STATE_MESSAGE);
			return;
		}

		// Get all writers according to topic
		List<WriterConfiguration> writerConfigurations = dataSourceToConfigsMap.get(configKey);

		if (writerConfigurations==null || writerConfigurations.isEmpty()) {
			logger.error("Couldn't find HDFS writer for key " + configKey + ". Dropping event");
            taskMetrics.HDFSWriterNotFoundMessages++;
			taskMonitoringHelper.countNewFilteredEvents(configKey, MonitorMessaages.NO_STATE_CONFIGURATION_MESSAGE);
			return;
		}

		//This parameter already
		boolean eventSuccessMonitored = false;

		// go over all writers and write message
		for (WriterConfiguration writerConfiguration : writerConfigurations) {

			// get the timestamp from the message
			Long timestamp = convertToLong(message.get(writerConfiguration.timestampField));
			if (timestamp == null) {
				// logger.error("message {} does not contains timestamp in field {}",
				// messageText, timestampField);
                writerConfiguration.tableWriterMetrics.invalidTimeFieldMessages++;
				taskMonitoringHelper.countNewFilteredEvents(configKey, MonitorMessaages.NO_TIMESTAMP_FIELD_IN_MESSAGE_label);
				throw new StreamMessageNotContainFieldException((String) envelope.getMessage(), writerConfiguration.timestampField);
			}

			// get the username from the message
			String username = convertToString(message.get(writerConfiguration.usernameField));

			// check if the event is before the time stamp barrier
			timestamp = TimestampUtils.convertToMilliSeconds(timestamp);
            writerConfiguration.tableWriterMetrics.messageEpoch = timestamp;

			if (writerConfiguration.barrier.isEventAfterBarrier(username, timestamp, message)) {

                // filter messages if needed
				if (filterMessage(message, writerConfiguration.filters)) {
                    writerConfiguration.tableWriterMetrics.filterFilteredMessages++;
					writerConfiguration.skippedMessageCount.inc();

				} else {
					// write the event to hdfs
					String eventLine = buildEventLine(message, writerConfiguration);
					writeLineToHdfs(writerConfiguration, eventLine, timestamp);
					writerConfiguration.tableWriterMetrics.writeToHdfsMessages++;
					writerConfiguration.processedMessageCount.inc();
					//We are lopping through each event one time or not.
					//If the event processed successfully at least once, we don't like to continue and count it more than once
					if (!eventSuccessMonitored) {
						handleUnfilteredEvent(message, configKey);
						eventSuccessMonitored = true;
					}
					// send to output topics
					List<String> outputTopics;
					if (!bdpService.isBDPRunning()) {
						outputTopics = writerConfiguration.outputTopics;
					} else {
						outputTopics = writerConfiguration.bdpOutputTopics;
					}
					if (outputTopics != null) {
						for (String outputTopic : outputTopics) {
							try {
								OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka",
									   outputTopic), message.toJSONString());
								collector.send(output);
                                writerConfiguration.tableWriterMetrics.writeToOutputTopicMessages++;
							} catch (Exception exception) {
								taskMonitoringHelper.countNewFilteredEvents(configKey, MonitorMessaages.FAILED_TO_SEND_EVENT_TO_KAFKA_LABEL);
                                writerConfiguration.tableWriterMetrics.writeToOutputTopicMessagesFailures++;
								throw new KafkaPublisherException(String.
								  format("failed to send event from input topic %s to output key %s after HDFS write",
										  configKey, outputTopic), exception);
							}
						}
					}
				}
				// update barrier
				writerConfiguration.barrier.updateBarrier(username, timestamp, message);
				// update timestamp counter
				writerConfiguration.lastTimestampCount.set(timestamp);
                writerConfiguration.tableWriterMetrics.barrierUpdates++;

			} else { //Event filter because of barrier
                writerConfiguration.tableWriterMetrics.barrierFilteredMessages++;
				taskMonitoringHelper.countNewFilteredEvents(configKey, MonitorMessaages.EVENT_OLDER_THEN_NEWEST_EVENT_LABEL);
			}
		}
	}


	/**
	 * filter message method that can be used by overriding instances to control 
	 * which messages are written
	 */
	private boolean filterMessage(JSONObject message, List<MessageFilter> filters) {
		for (MessageFilter filter : filters) {
			if (filter.filter(message)) {
				if (filter.monitorIfFiltered()) {
					taskMonitoringHelper.countNewFilteredEvents(extractDataSourceConfigKey(message), MonitorMessaages.MessageFilter, filter.getName());
				}
				return true;
			}
		}
		return false;
	}

	private String buildEventLine(JSONObject message, WriterConfiguration writerConfiguration) {
		StringBuilder line = new StringBuilder();
		boolean first = true;
		for (String field : writerConfiguration.fields) {
			if (first)
				first = false;
			else
				line.append(writerConfiguration.separator);

			Object value = writerConfiguration.featureExtractionService.extract(field, message);
			if (value != null)
				line.append(value.toString());
		}
		return line.toString();
	}


	/** Periodically flush data to hdfs */
	@Override
	public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

		logger.info("Flushing HDFS data..");
		// flush all writers
		for (List<WriterConfiguration> writerConfigurations : dataSourceToConfigsMap.values()) {
			for (WriterConfiguration writerConfiguration : writerConfigurations) {
				// flush writes to hdfs and refresh impala
				writerConfiguration.service.flushHdfs();

				writerConfiguration.barrier.flushBarrier();

                writerConfiguration.tableWriterMetrics.flushes++;
			}
		}
		logger.info("Finished flushing HDFS data");

		// commit the checkpoint in the kafka topic when flushing events, not to
		// write them twice
		coordinateCheckpoint(coordinator);
	}

	private void coordinateCheckpoint(TaskCoordinator coordinator) throws TaskCoordinatorException {
		try {
            taskMetrics.coordinate++;
			coordinator.commit(RequestScope.CURRENT_TASK);
		} catch (Exception exception) {
            taskMetrics.coordinateExceptions++;
            throw new TaskCoordinatorException("failed to commit the checkpoint in to the kafka topic", exception);
		}
	}

	/** Close the hdfs writer when job shuts down */
	@Override
	protected void wrappedClose() throws Exception {

		for (List<WriterConfiguration> writerConfigurations : dataSourceToConfigsMap.values()) {
			for (WriterConfiguration writerConfiguration : writerConfigurations) {
				// close hdfs appender
				if (writerConfiguration.service != null) {
					writerConfiguration.service.close();
					writerConfiguration.barrier.flushBarrier();
				}
				writerConfiguration.service = null;
				writerConfiguration.barrier = null;
			}
		}

	}

	@Override	
	protected String getJobLabel() {
		return "HDFSWriterStreamTask";
	}

	/**
	 * Private class for configuration of specific writer (specific topic, specific HDFS file)
	 */
	private class WriterConfiguration {
		private String timestampField;
		private String usernameField;
		private List<String> fields;
		private String separator;
		private HdfsService service;
		private String tableName;
        private HDFSWriterStreamingTaskTableWriterMetrics tableWriterMetrics;
		private Counter processedMessageCount;
		private Counter skippedMessageCount;
		private Counter lastTimestampCount;
		private String storeName;
		private BarrierService barrier;
		private List<MessageFilter> filters = new LinkedList<>();
		private PartitionStrategy partitionStrategy;
		private List<String> outputTopics;
		private List<String> bdpOutputTopics;
		private FeatureExtractionService featureExtractionService;
	}

	/**
	 * Create the task's specific metrics.
	 *
	 * Typically, the function is called from AbstractStreamTask.createTaskMetrics() at init()
	 */
	@Override
	protected void wrappedCreateTaskMetrics() {

		// Create the task's specific metrics
		taskMetrics = new HDFSWriterStreamingTaskMetrics(statsService);
	}

	/**
	 * Try writing an event to HDFS using the {@link EnhancedExceptionHandler}.
	 */
	private void writeLineToHdfs(WriterConfiguration writerConfiguration, String eventLine, Long timestamp)
			throws Exception {

		try {
			// Try to write the event to HDFS
			writerConfiguration.service.writeLineToHdfs(eventLine, timestamp);
			// The write operation was successful - reset the number of consecutive failures
			enhancedExceptionHandler.reset();
		} catch (Exception eFirstTry) {
			// The write operation was not successful
			logger.error(String.format("Failed to write event %s with timestamp %d on first try.",
					eventLine, timestamp), eFirstTry);
			boolean retry;

			try {
				// Handle the exception and check if there should be a retry
				retry = enhancedExceptionHandler.handleException(eFirstTry);
			} catch (Exception eRethrown) {
				// The handler threw back the exception - throw it as well
				taskMetrics.writeExceptionsThrown++;
				logger.error("Not retrying to write the event to HDFS and throwing the exception.");
				throw eRethrown;
			}

			if (retry) {
				try {
					// Retry to write the event to HDFS
					taskMetrics.writeRetries++;
					logger.info("Retrying to write the event to HDFS.");
					writerConfiguration.service.writeLineToHdfs(eventLine, timestamp);
				} catch (Exception eRetry) {
					// The second attempt to write the event to HDFS also failed
					taskMetrics.failedWriteRetries++;
					logger.error("Failed to write the event to HDFS on retry.", eRetry);
				}
			} else {
				// No retry required - do not write the event to HDFS and discard it
				taskMetrics.eventsDiscarded++;
				logger.info("Not retrying to write the event to HDFS and discarding it.");
			}
		}
	}
}
