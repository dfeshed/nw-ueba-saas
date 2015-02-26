package fortscale.streaming.task;

import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.exceptions.TaskCoordinatorException;
import fortscale.streaming.filters.MessageFilter;
import fortscale.ml.model.prevalance.UserTimeBarrier;
import fortscale.streaming.service.BarrierService;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.HdfsService;
import fortscale.streaming.service.SpringService;
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

import java.util.LinkedList;
import java.util.List;

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



	private String timestampField;
	private String usernameField;
	private List<String> fields;
	private String separator;
	private HdfsService service;
	private String tableName;
	private Counter processedMessageCount;
	private Counter skipedMessageCount;
	private Counter lastTimestampCount;
	private String storeName;
	private BarrierService barrier;
	private List<MessageFilter> filters = new LinkedList<MessageFilter>();
    private PartitionStrategy partitionStrategy;


    /** reads task configuration from job config and initialize hdfs appender */
	@SuppressWarnings("unchecked")
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		// Get configuration properties
		FortscaleStringValueResolver res = SpringService.getInstance().resolve(FortscaleStringValueResolver.class);

		timestampField = resolveStringValue(config, "fortscale.timestamp.field", res);
		usernameField = resolveStringValue(config, "fortscale.username.field", res);
		List<String> discriminatorsFields = resolveStringValues(config, "fortscale.discriminator.fields", res);
		fields = ImpalaParser.getTableFieldNames(resolveStringValue(config, "fortscale.fields", res));
		separator = resolveStringValueDefault(config, "fortscale.separator", ",", res);
		String hdfsRootPath = resolveStringValue(config, "fortscale.hdfs.root", res);
		tableName = resolveStringValue(config, "fortscale.table.name", res);
		String fileName = resolveStringValue(config, "fortscale.file.name", res);
		partitionStrategy = PartitionsUtils.getPartitionStrategy(resolveStringValue(config, "fortscale.partition.strategy", res));
		String splitClassName = resolveStringValue(config, "fortscale.split.strategy", res);
		int eventsCountFlushThreshold = config.getInt("fortscale.events.flush.threshold");
		long windowDuration = config.getLong("task.window.ms");

		storeName = storeNamePrefix + tableName;
		FileSplitStrategy splitStrategy = (FileSplitStrategy)Class.forName(splitClassName).newInstance();

		// create HDFS appender service
		service = new HdfsService(hdfsRootPath, fileName, partitionStrategy, splitStrategy, tableName, eventsCountFlushThreshold, windowDuration);

		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-write-count", tableName));
		skipedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-skip-count", tableName));
		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-epochime", tableName));

		// get write time stamp barrier store
		barrier = new BarrierService((KeyValueStore<String, UserTimeBarrier>) context.getStore(storeName), discriminatorsFields);
		
		// load filters from configuration
		for (String filterName : config.getList("fortscale.filters", new LinkedList<String>())) {
			// create a filter instance
			String filterClass = getConfigString(config, String.format("fortscale.filter.%s.class", filterName));
			MessageFilter filter = (MessageFilter)Class.forName(filterClass).newInstance();
			
			// initialize the filter with configuration
			filter.init(filterName, config);
			filters.add(filter);
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

		// get the timestamp from the message
		Long timestamp = convertToLong(message.get(timestampField));
		if (timestamp == null) {
			// logger.error("message {} does not contains timestamp in field {}",
			// messageText, timestampField);
			throw new StreamMessageNotContainFieldException(messageText, timestampField);
		}

		// get the username from the message
		String username = convertToString(message.get(usernameField));
	
		// check if the event is before the time stamp barrier
		timestamp = TimestampUtils.convertToMilliSeconds(timestamp);
		if (barrier.isEventAfterBarrier(username, timestamp, message)) {
			// filter messages if needed
			if (filterMessage(message)) {
				skipedMessageCount.inc();
			} else {
				// write the event to hdfs
				String eventLine = buildEventLine(message);
				service.writeLineToHdfs(eventLine, timestamp.longValue());
				processedMessageCount.inc();
			}
			// update barrier
			barrier.updateBarrier(username, timestamp, message);
			// update timestamp counter
			lastTimestampCount.set(timestamp);
		}
	}
	
	/**
	 * filter message method that can be used by overriding instances to control 
	 * which messages are written
	 */
	private boolean filterMessage(JSONObject message) {
		for (MessageFilter filter : filters) {
			if (filter.filter(message))
				return true;
		}
		return false;
	}

	private String buildEventLine(JSONObject message) {
		StringBuilder line = new StringBuilder();
		boolean first = true;
		for (String field : fields) {
			if (first)
				first = false;
			else
				line.append(separator);

			Object value = message.get(field);
			if (value != null)
				line.append(value.toString());
		}
		return line.toString();
	}


	/** Periodically flush data to hdfs */
	@Override
	public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// flush writes to hdfs and refresh impala
		service.flushHdfs();

		barrier.flushBarrier();
		// commit the checkpoint in the kafka topic when flushing events, not to
		// write them twice
		coordinateCheckpoint(coordinator);
	}

	private void coordinateCheckpoint(TaskCoordinator coordinator) throws TaskCoordinatorException {
		try {
			coordinator.commit(RequestScope.CURRENT_TASK);
		} catch (Exception exception) {
			throw new TaskCoordinatorException(String.format("failed to commit the checkpoint in to the kafka topic. tablename: %s", tableName), exception);
		}
	}

	/** Close the hdfs writer when job shuts down */
	@Override
	protected void wrappedClose() throws Exception {
		// close hdfs appender
		if (service != null) {
			service.close();
			barrier.flushBarrier();
		}
		service = null;
		barrier = null;
	}

}
