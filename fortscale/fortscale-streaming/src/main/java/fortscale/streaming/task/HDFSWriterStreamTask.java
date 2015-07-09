package fortscale.streaming.task;

import com.google.common.collect.Iterables;
import fortscale.ml.model.prevalance.UserTimeBarrier;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.exceptions.TaskCoordinatorException;
import fortscale.streaming.feature.extractor.FeatureExtractionService;
import fortscale.streaming.filters.MessageFilter;
import fortscale.streaming.service.BarrierService;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.HdfsService;
import fortscale.streaming.service.SpringService;
import fortscale.utils.StringPredicates;
import fortscale.utils.TimestampUtils;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.impala.ImpalaParser;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.apache.samza.task.TaskCoordinator.RequestScope;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
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
	private static Logger logger = LoggerFactory.getLogger(HDFSWriterStreamTask.class);

	/**
	 * Map from input topic to all relevant HDFS writes (can be more than 1, for example: for regular and "top" tables)
	 */
	protected Map<String, List<WriterConfiguration>> topicToWriterConfigurationMap = new HashMap<>();

	/**
	 * Private class for configuration of specific writer (specific topic, specific HDFS file)
	 */
	private class WriterConfiguration {

		public String timestampField;
		public String usernameField;
		public List<String> fields;
		public String separator;
		public HdfsService service;
		public String tableName;
		public Counter processedMessageCount;
		public Counter skipedMessageCount;
		public Counter lastTimestampCount;
		public String storeName;
		public BarrierService barrier;
		public List<MessageFilter> filters = new LinkedList<>();
		public PartitionStrategy partitionStrategy;

		public FeatureExtractionService featureExtractionService;

	}


    /** reads task configuration from job config and initialize hdfs appender */
	@SuppressWarnings("unchecked")
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		FortscaleStringValueResolver res = SpringService.getInstance().resolve(FortscaleStringValueResolver.class);

		long windowDuration = config.getLong("task.window.ms");

		// Get configuration properties
		Config fieldsSubset = config.subset("fortscale.");
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".input.topic"))) {
			String eventType = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".input.topic"));

			// create specific configuration for topic
			WriterConfiguration writerConfiguration = new WriterConfiguration();
			String inputTopic = resolveStringValue(config, String.format("fortscale.%s.input.topic", eventType), res);
			if (!topicToWriterConfigurationMap.containsKey(inputTopic)) {
				topicToWriterConfigurationMap.put(inputTopic, new ArrayList<WriterConfiguration>());
			}
			topicToWriterConfigurationMap.get(inputTopic).add(writerConfiguration);

			// read configuration properties

			writerConfiguration.timestampField = resolveStringValue(config, String.format("fortscale.%s.timestamp.field", eventType), res);
			writerConfiguration.usernameField = resolveStringValue(config, String.format("fortscale.%s.username.field", eventType), res);
			List<String> discriminatorsFields = resolveStringValues(config, String.format("fortscale.%s.discriminator.fields", eventType), res);
			writerConfiguration.fields = ImpalaParser.getTableFieldNames(resolveStringValue(config, String.format("fortscale.%s.fields", eventType), res));
			writerConfiguration.separator = resolveStringValueDefault(config, String.format("fortscale.%s.separator", eventType), ",", res);
			String hdfsRootPath = resolveStringValue(config, String.format("fortscale.%s.hdfs.root", eventType), res);
			writerConfiguration.tableName = resolveStringValue(config, String.format("fortscale.%s.table.name", eventType), res);
			String fileName = resolveStringValue(config, String.format("fortscale.%s.file.name", eventType), res);
			writerConfiguration.partitionStrategy = PartitionsUtils.getPartitionStrategy(resolveStringValue(config, String.format("fortscale.%s.partition.strategy", eventType), res));
			String splitClassName = resolveStringValue(config, String.format("fortscale.%s.split.strategy", eventType), res);
			int eventsCountFlushThreshold = config.getInt(String.format("fortscale.%s.events.flush.threshold", eventType));


			writerConfiguration.storeName = storeNamePrefix + writerConfiguration.tableName;
			FileSplitStrategy splitStrategy = (FileSplitStrategy) Class.forName(splitClassName).newInstance();

			// create HDFS appender service
			writerConfiguration.service = new HdfsService(hdfsRootPath, fileName, writerConfiguration.partitionStrategy,
					splitStrategy, writerConfiguration.tableName, eventsCountFlushThreshold, windowDuration, writerConfiguration.separator);
			writerConfiguration.featureExtractionService = new FeatureExtractionService(config, String.format("fortscale.%s.feature.extractor.", eventType));

			// create counter metric for processed messages
			writerConfiguration.processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-write-count", writerConfiguration.tableName));
			writerConfiguration.skipedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-skip-count", writerConfiguration.tableName));
			writerConfiguration.lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-epochime", writerConfiguration.tableName));

			// get write time stamp barrier store
			writerConfiguration.barrier = new BarrierService((KeyValueStore<String, UserTimeBarrier>) context.getStore(writerConfiguration.storeName), discriminatorsFields);

			// load filters from configuration
			for (String filterName : config.getList(String.format("fortscale.%s.filters", eventType), new LinkedList<String>())) {
				// create a filter instance
				String filterClass = getConfigString(config, String.format("fortscale.%s.filter.%s.class", eventType, filterName));
				MessageFilter filter = (MessageFilter) Class.forName(filterClass).newInstance();

				// initialize the filter with configuration
				filter.init(filterName, config, eventType);
				writerConfiguration.filters.add(filter);
			}

			logger.info(String.format("Finished loading configuration for table %s (topic: %s) ", writerConfiguration.tableName, inputTopic));

		}
	}

	private String resolveStringValue(Config config, String string, FortscaleStringValueResolver resolver) {
		return resolver.resolveStringValue(getConfigString(config, string));
	}

	private List<String> resolveStringValues(Config config, String string, FortscaleStringValueResolver resolver) {
		return resolver.resolveStringValues(getConfigStringList(config, string));
	}

	private String resolveStringValueDefault(Config config, String string, String def, FortscaleStringValueResolver resolver) {
		return resolver.resolveStringValue(config.get(string, def));
	}

	/** Write the incoming message fields to hdfs */
	@Override
	public void wrappedProcess(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		// parse the message into json
		String messageText = (String) envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		// Get the input topic
		String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		// Get all writers according to topic
		List<WriterConfiguration> writerConfigurations = topicToWriterConfigurationMap.get(topic);

		if (writerConfigurations.isEmpty()) {
			logger.error("Couldn't find HDFS writer for topic " + topic + ". Dropping event");
		}

		// go over all writers and write message
		for (WriterConfiguration writerConfiguration : writerConfigurations) {

			// get the timestamp from the message
			Long timestamp = convertToLong(message.get(writerConfiguration.timestampField));
			if (timestamp == null) {
				// logger.error("message {} does not contains timestamp in field {}",
				// messageText, timestampField);
				throw new StreamMessageNotContainFieldException(messageText, writerConfiguration.timestampField);
			}

			// get the username from the message
			String username = convertToString(message.get(writerConfiguration.usernameField));

			// check if the event is before the time stamp barrier
			timestamp = TimestampUtils.convertToMilliSeconds(timestamp);
			if (writerConfiguration.barrier.isEventAfterBarrier(username, timestamp, message)) {
				// filter messages if needed
				if (filterMessage(message, writerConfiguration.filters)) {
					writerConfiguration.skipedMessageCount.inc();
				} else {
					// write the event to hdfs
					String eventLine = buildEventLine(message, writerConfiguration);
					writerConfiguration.service.writeLineToHdfs(eventLine, timestamp.longValue());
					writerConfiguration.processedMessageCount.inc();
				}
				// update barrier
				writerConfiguration.barrier.updateBarrier(username, timestamp, message);
				// update timestamp counter
				writerConfiguration.lastTimestampCount.set(timestamp);
			}
		}
	}
	
	/**
	 * filter message method that can be used by overriding instances to control 
	 * which messages are written
	 */
	private boolean filterMessage(JSONObject message, List<MessageFilter> filters) {
		for (MessageFilter filter : filters) {
			if (filter.filter(message))
				return true;
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

		// flush all writers
		for (List<WriterConfiguration> writerConfigurations : topicToWriterConfigurationMap.values()) {
			for (WriterConfiguration writerConfiguration : writerConfigurations) {
				// flush writes to hdfs and refresh impala
				writerConfiguration.service.flushHdfs();

				writerConfiguration.barrier.flushBarrier();
			}
		}

		// commit the checkpoint in the kafka topic when flushing events, not to
		// write them twice
		coordinateCheckpoint(coordinator);
	}

	private void coordinateCheckpoint(TaskCoordinator coordinator) throws TaskCoordinatorException {
		try {
			coordinator.commit(RequestScope.CURRENT_TASK);
		} catch (Exception exception) {
			throw new TaskCoordinatorException(String.format("failed to commit the checkpoint in to the kafka topic"), exception);
		}
	}

	/** Close the hdfs writer when job shuts down */
	@Override
	protected void wrappedClose() throws Exception {

		for (List<WriterConfiguration> writerConfigurations : topicToWriterConfigurationMap.values()) {
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

}
