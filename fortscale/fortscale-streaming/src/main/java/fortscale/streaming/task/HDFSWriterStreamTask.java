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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.streaming.exceptions.LevelDbException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.exceptions.TaskCoordinatorException;
import fortscale.streaming.model.prevalance.UserTimeBarrierModel;
import fortscale.streaming.service.HdfsService;
import fortscale.utils.TimestampUtils;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;

/**
 * Stream tasks that receives events and write them to hdfs using a partitioned
 * writer
 */
public class HDFSWriterStreamTask extends AbstractStreamTask implements
		InitableTask, ClosableTask {

	private static final Logger logger = LoggerFactory.getLogger(HDFSWriterStreamTask.class);

	private static final String storeNamePrefix = "hdfs-write-";

	private String timestampField;
	private String usernameField;
	private List<String> fields;
	private List<String> discriminatorsFields;
	private String separator;
	private HdfsService service;
	private String tableName;
	private Counter processedMessageCount;
	private KeyValueStore<String, UserTimeBarrierModel> store;
	private String storeName;

	/** reads task configuration from job config and initialize hdfs appender */
	@Override
	public void init(Config config, TaskContext context) throws Exception {
		// get configuration properties
		String hdfsRootPath = getConfigString(config, "fortscale.hdfs.root");
		String fileName = getConfigString(config, "fortscale.file.name");
		separator = config.get("fortscale.separator", ",");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		usernameField = getConfigString(config, "fortscale.username.field");
		fields = getConfigStringList(config, "fortscale.fields");
		discriminatorsFields = getConfigStringList(config, "fortscale.discriminator.fields");
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
		loadBarrier(context);
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
		String discriminator = calculateDiscriminator(message);

		// check if the event is before the time stamp barrier
		timestamp = TimestampUtils.convertToMilliSeconds(timestamp);
		if (isEventAfterBarrier(username, timestamp, discriminator)) {

			// write the event to hdfs
			String eventLine = buildEventLine(message);
			service.writeLineToHdfs(eventLine, timestamp.longValue());

			updateBarrier(username, timestamp, discriminator);
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
	public void wrappedWindow(MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
		// flush writes to hdfs and refresh impala
		service.flushHdfs();

		flushBarrier();
		// commit the checkpoint in the kafka topic when flushing events, not to
		// write them twice
		coordinateCheckpoint(coordinator);
	}

	private void coordinateCheckpoint(TaskCoordinator coordinator)
			throws TaskCoordinatorException {
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
			flushBarrier();
		}
		service = null;
	}

	// //////////// events time stamp write barrier methods

	private String calculateDiscriminator(JSONObject message) {
		StringBuilder sb = new StringBuilder();
		for (String field : discriminatorsFields) {
			sb.append(convertToString(message.get(field)));
			sb.append(";");
		}
		return sb.toString();
	}

	private boolean isEventAfterBarrier(String username, long timestamp,
			String discriminator) {
		// get the barrier from the state, stored by table name
		UserTimeBarrierModel barrier = store.get(username);
		if (barrier == null)
			return true;

		return ((timestamp > barrier.getTimestamp()) || (timestamp == barrier.getTimestamp() && !barrier.getDiscriminator().equals(discriminator)));
	}

	private void updateBarrier(String username, long timestamp,
			String discriminator) throws LevelDbException {
		if (username == null)
			return;

		UserTimeBarrierModel barrier = store.get(username);
		if (barrier == null)
			barrier = new UserTimeBarrierModel();

		// update barrier in case it is not too much in the future
		if (!TimestampUtils.isFutureTimestamp(timestamp, 24)) {
			barrier.setTimestamp(timestamp);
			barrier.setDiscriminator(discriminator);
			try {
				store.put(username, barrier);
			} catch (Exception exception) {
				logger.error("error storing value. username: {} exception: {}", username, exception);
				logger.error("error storing value.", exception);
				throw new LevelDbException(String.format("error while trying to store user %s.", username), exception);
			}
		} else {
			logger.error("encountered event in a future time {} [current time={}] for user {}, skipping barrier update", timestamp, System.currentTimeMillis(), username);
		}
	}

	private void flushBarrier() throws LevelDbException {
		try {
			store.flush();
		} catch (Exception exception) {
			logger.error("error flushing. tablename: {} exception: {}", tableName, exception);
			logger.error("error flushing.", exception);
			throw new LevelDbException(String.format("error while trying to do store fulsh. tablename: %s", tableName), exception);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadBarrier(TaskContext context) {
		store = (KeyValueStore<String, UserTimeBarrierModel>) context.getStore(storeName);
	}
}
