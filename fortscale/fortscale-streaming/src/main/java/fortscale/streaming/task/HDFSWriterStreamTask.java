package fortscale.streaming.task;

import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;

import java.util.List;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.samza.config.Config;
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
	
	private String timestampField;
	private List<String> fields;
	private String separator;
	private HdfsService service;
	private String tableName;
	private int eventsCountFlushThreshold;
	private int nonFlushedEventsCounter = 0;
	
	/** reads task configuration from job config and initialize hdfs appender */
	@Override public void init(Config config, TaskContext context) throws Exception {
		// get configuration properties
		String hdfsRootPath = getConfigString(config, "fortscale.hdfs.root");
		String fileName = getConfigString(config, "fortscale.file.name");
		separator = config.get("fortscale.separator", ",");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		fields = getConfigStringList(config, "fortscale.fields");
		tableName = getConfigString(config, "fortscale.table.name");
		eventsCountFlushThreshold = config.getInt("fortscale.events.flush.threshold");
		
		String partitionClassName = getConfigString(config, "fortscale.partition.strategy");
		PartitionStrategy partitionStrategy = (PartitionStrategy) Class.forName(partitionClassName).newInstance();
		String splitClassName = getConfigString(config, "fortscale.split.strategy");
		FileSplitStrategy splitStrategy = (FileSplitStrategy) Class.forName(splitClassName).newInstance();
		
		// create HDFS appender service
		service = new HdfsService(hdfsRootPath, fileName, partitionStrategy, splitStrategy, tableName);
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

			// write the event to hdfs
			String eventLine = buildEventLine(message);
			service.writeLineToHdfs(eventLine, timestamp.longValue());
			
			nonFlushedEventsCounter++;
			if (nonFlushedEventsCounter>=eventsCountFlushThreshold) {
				flushEvents();
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
	}
	
	/** Periodically flush data to hdfs */
	@Override public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		flushEvents();
	}

	/** Close the hdfs writer when job shuts down */
	@Override public void close() throws Exception {
		// close hdfs appender
		if (service!=null)
			service.close();
		service = null;
	}	

}
