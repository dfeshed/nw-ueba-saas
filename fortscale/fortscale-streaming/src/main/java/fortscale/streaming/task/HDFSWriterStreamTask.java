package fortscale.streaming.task;

import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.List;

import fortscale.streaming.model.prevalance.UserTimeBarrierModel;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.streaming.service.HdfsService;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;

/**
 * Stream tasks that receives events and write them to hdfs using 
 * a partitioned writer 
 */
public class HDFSWriterStreamTask implements StreamTask, InitableTask, ClosableTask, WindowableTask {

	private static final Logger logger = LoggerFactory.getLogger(HDFSWriterStreamTask.class);
	
	private static final String storeNamePrefix = "hdfs-write-";
	
	private String timestampField;
    private String usernameField;
	private List<String> fields;
    private List<String> discriminatorsFields;
	private String separator;
	private HdfsService service;
	private String tableName;
	private int eventsCountFlushThreshold;
	private int nonFlushedEventsCounter = 0;
	private Counter processedMessageCount;
	private KeyValueStore<String, UserTimeBarrierModel> store;
    private String storeName;
	
	/** reads task configuration from job config and initialize hdfs appender */
	@Override public void init(Config config, TaskContext context) throws Exception {
		// get configuration properties
		String hdfsRootPath = getConfigString(config, "fortscale.hdfs.root");
		String fileName = getConfigString(config, "fortscale.file.name");
		separator = config.get("fortscale.separator", ",");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
        usernameField = getConfigString(config, "fortscale.username.field");
		fields = getConfigStringList(config, "fortscale.fields");
        discriminatorsFields = getConfigStringList(config, "fortscale.discriminator.fields");
		tableName = getConfigString(config, "fortscale.table.name");
		eventsCountFlushThreshold = config.getInt("fortscale.events.flush.threshold");
		storeName = storeNamePrefix + tableName;

		String partitionClassName = getConfigString(config, "fortscale.partition.strategy");
		PartitionStrategy partitionStrategy = (PartitionStrategy) Class.forName(partitionClassName).newInstance();
		String splitClassName = getConfigString(config, "fortscale.split.strategy");
		FileSplitStrategy splitStrategy = (FileSplitStrategy) Class.forName(splitClassName).newInstance();
		
		// create HDFS appender service
		service = new HdfsService(hdfsRootPath, fileName, partitionStrategy, splitStrategy, tableName);
		
		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-events-write-count", tableName));
		
		// get write time stamp barrier store
		loadBarrier(context);
	}
	
	/** Write the incoming message fields to hdfs */
	@Override public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		try {
			// parse the message into json 
			String messageText = (String)envelope.getMessage();
			JSONObject message = (JSONObject) JSONValue.parse(messageText);
			if (message==null) {
				logger.error("message in envelope cannot be parsed - {}", messageText);
				return;
			}
			
			// get the timestamp from the message
			Long timestamp = convertToLong(message.get(timestampField));
			if (timestamp==null) {
				logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
				return;
			}

            // get the username from the message
            String username = convertToString(message.get(usernameField));
            String discriminator = calculateDiscriminator(message);

			// check if the event is before the time stamp barrier
			if (isEventAfterBarrier(username, timestamp, discriminator)) {
			
				// write the event to hdfs
				String eventLine = buildEventLine(message);
				service.writeLineToHdfs(eventLine, timestamp.longValue());
				
				updateBarrier(username, timestamp, discriminator);
				processedMessageCount.inc();
				nonFlushedEventsCounter++;
				
				if (nonFlushedEventsCounter>=eventsCountFlushThreshold) {
					flushEvents();
				}
			}
			
		} catch (Exception e) {
			logger.error("error while writing events to " + tableName + " with mesage " + envelope.getMessage(), e);
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
			if (value!=null)
				line.append(value.toString());
		}
		return line.toString();
	}

	private void flushEvents() throws Exception {
		// flush writes to hdfs and refresh impala
		service.flushHdfs();
		nonFlushedEventsCounter = 0;
		flushBarrier();
	}
	
	/** Periodically flush data to hdfs */
	@Override public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		flushEvents();
	}

	/** Close the hdfs writer when job shuts down */
	@Override public void close() throws Exception {
		// close hdfs appender
		if (service!=null) {
			service.close();
			flushBarrier();
		}
		service = null;
	}	
	
	////////////// events time stamp write barrier methods

    private String calculateDiscriminator(JSONObject message) {
        StringBuilder sb = new StringBuilder();
        for (String field : discriminatorsFields) {
            sb.append(convertToString(message.get(field)));
            sb.append(";");
        }
        return sb.toString();
    }

	private boolean isEventAfterBarrier(String username, long timestamp, String discriminator) {
		// get the barrier from the state, stored by table name
        UserTimeBarrierModel barrier = store.get(username);
        if (barrier==null)
            return true;

        return ((timestamp > barrier.getTimestamp()) ||
                (timestamp == barrier.getTimestamp() && !barrier.getDiscriminator().equals(discriminator)));
	}
	
	private void updateBarrier(String username, long timestamp, String discriminator) {
        if (username==null) return;

        UserTimeBarrierModel barrier = store.get(username);
        if (barrier==null)
            barrier = new UserTimeBarrierModel();

        barrier.setTimestamp(timestamp);
        barrier.setDiscriminator(discriminator);
        store.put(username, barrier);
	}
	
	private void flushBarrier() {
        store.flush();
	}
	
	@SuppressWarnings("unchecked")
	private void loadBarrier(TaskContext context) {
		store = (KeyValueStore<String, UserTimeBarrierModel>)context.getStore(storeName);
	}
}
