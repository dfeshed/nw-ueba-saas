package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.List;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.TaskCoordinator.RequestScope;

import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.exceptions.TaskCoordinatorException;
import fortscale.streaming.model.prevalance.UserTimeBarrierModel;
import fortscale.streaming.service.BarrierService;
import fortscale.streaming.service.HdfsService;
import fortscale.utils.TimestampUtils;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;

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
	private String storeName;
	private BarrierService barrier;

	/** reads task configuration from job config and initialize hdfs appender */
	@SuppressWarnings("unchecked")
	@Override
	public void init(Config config, TaskContext context) throws Exception {
		// get configuration properties
		String hdfsRootPath = getConfigString(config, "fortscale.hdfs.root");
		String fileName = getConfigString(config, "fortscale.file.name");
		separator = config.get("fortscale.separator", ",");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		usernameField = getConfigString(config, "fortscale.username.field");
		fields = getConfigStringList(config, "fortscale.fields");
		List<String> discriminatorsFields = getConfigStringList(config, "fortscale.discriminator.fields");
		tableName = getConfigString(config, "fortscale.table.name");
		int eventsCountFlushThreshold = config.getInt("fortscale.events.flush.threshold");
		storeName = storeNamePrefix + tableName;

		String partitionClassName = getConfigString(config, "fortscale.partition.strategy");
		PartitionStrategy partitionStrategy = (PartitionStrategy) Class.forName(partitionClassName).newInstance();
		String splitClassName = getConfigString(config, "fortscale.split.strategy");
		FileSplitStrategy splitStrategy = (FileSplitStrategy) Class.forName(splitClassName).newInstance();

		// create HDFS appender service
		service = new HdfsService(hdfsRootPath, fileName, partitionStrategy, splitStrategy, tableName, eventsCountFlushThreshold);

		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-write-count", tableName));

		// get write time stamp barrier store
		barrier = new BarrierService((KeyValueStore<String, UserTimeBarrierModel>) context.getStore(storeName), discriminatorsFields);
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
			// write the event to hdfs
			String eventLine = buildEventLine(message);
			service.writeLineToHdfs(eventLine, timestamp.longValue());

			// update barrier
			barrier.updateBarrier(username, timestamp, message);
			processedMessageCount.inc();
		}
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
	public void close() throws Exception {
		// close hdfs appender
		if (service != null) {
			service.close();
			barrier.flushBarrier();
		}
		service = null;
		barrier = null;
	}

}
