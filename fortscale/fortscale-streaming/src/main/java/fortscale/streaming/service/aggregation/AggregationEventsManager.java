package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToLong;

public class AggregationEventsManager {
	private static final Logger logger = LoggerFactory.getLogger(AggregationEventsManager.class);

	private String timestampFieldName;
	private FeatureBucketsService featureBucketsService;

	public AggregationEventsManager(Config config) {
		timestampFieldName = getConfigString(config, "fortscale.timestamp.field");
		featureBucketsService = new FeatureBucketsService();
	}

	public void processEvent(IncomingMessageEnvelope envelope) throws Exception {
		String messageText = (String)envelope.getMessage();
		JSONObject messageJson = (JSONObject)JSONValue.parseWithException(messageText);

		Long timestamp = convertToLong(messageJson.get(timestampFieldName));
		if (timestamp == null) {
			logger.warn("Event message {} contains no timestamp in field {}", messageText, timestampFieldName);
			return;
		}

		featureBucketsService.updateFeatureBuckets(messageJson, timestamp);
	}

	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// TODO implement
	}

	public void close() throws Exception {
		// TODO implement
	}
}
