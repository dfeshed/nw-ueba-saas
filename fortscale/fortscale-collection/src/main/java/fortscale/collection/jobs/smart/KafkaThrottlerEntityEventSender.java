package fortscale.collection.jobs.smart;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.IEntityEventSender;
import fortscale.utils.ConversionUtils;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.MetricsReader;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Configurable(preConstruction = true)
public class KafkaThrottlerEntityEventSender implements IEntityEventSender {
	private static final Logger logger = Logger.getLogger(KafkaThrottlerEntityEventSender.class);
	private static final int MILLISECONDS_TO_WAIT = 60 * 1000;

	@Autowired
	private EntityEventConfService entityEventConfService;

	@Value("${streaming.event.field.type.entity_event}")
	private String eventType;
	@Value("${fortscale.aggregation.events.counter.name.suffix}")
	private String counterNameSuffix;
	@Value("${broker.list}")
	private String brokerList;
	@Value("${fortscale.aggregation.events.counter.creation.job}")
	private String counterCreationJob;
	@Value("${fortscale.aggregation.events.counter.creation.class}")
	private String counterCreationClass;
	@Value("${kafka.entity.event.topic}")
	private String outputTopicName;

	private int batchSize;
	private int checkRetries;
	private List<JSONObject> entityEvents;
	private List<String> metricsOfEntityEvents;
	private String zookeeper;
	private int port;
	private KafkaEventsWriter kafkaEventsWriter;

	public KafkaThrottlerEntityEventSender(int batchSize, int checkRetries) {
		Assert.isTrue(batchSize > 0);
		Assert.isTrue(checkRetries > 0);
		String[] brokerListSplit = brokerList.split(":");

		this.batchSize = batchSize;
		this.checkRetries = checkRetries;
		this.entityEvents = new ArrayList<>();
		this.metricsOfEntityEvents = new ArrayList<>();
		this.zookeeper = brokerListSplit[0];
		this.port = Integer.parseInt(brokerListSplit[1]);
		this.kafkaEventsWriter = new KafkaEventsWriter(outputTopicName);

		for (EntityEventConf entityEventConf : entityEventConfService.getEntityEventDefinitions()) {
			metricsOfEntityEvents.add(getCounterName(entityEventConf));
		}
	}

	@Override
	public void send(JSONObject entityEvent) {
		if (entityEvent == null) {
			return;
		} else {
			entityEvents.add(entityEvent);
		}

		if (entityEvents.size() == batchSize) {
			long initialCounterMetricsSum = getCounterMetricsSum();
			sendEntityEvents();
			ReachSumMetricsDecider decider = new ReachSumMetricsDecider(
					metricsOfEntityEvents, initialCounterMetricsSum + batchSize);
			boolean result = MetricsReader.waitForMetrics(
					zookeeper, port, counterCreationClass, counterCreationJob,
					decider, MILLISECONDS_TO_WAIT, checkRetries);

			if (!result) {
				String errorMsg = "Waiting for processing of entity events timed out.";
				logger.error(errorMsg);
				throw new RuntimeException(errorMsg);
			}
		}
	}

	@Override
	public void close() {
		sendEntityEvents();
	}

	private String getCounterName(EntityEventConf entityEventConf) {
		return String.format("%s.%s%s", eventType, entityEventConf.getName(), counterNameSuffix);
	}

	private long getCounterMetricsSum() {
		long counterMetricsSum = 0;

		for (String metric : metricsOfEntityEvents) {
			CaptorMetricsDecider captor = new CaptorMetricsDecider(metric);
			boolean result = MetricsReader.waitForMetrics(
					zookeeper, port, counterCreationClass, counterCreationJob,
					captor, MILLISECONDS_TO_WAIT, checkRetries);
			Long counter = ConversionUtils.convertToLong(captor.getCapturedMetric());

			if (result && counter != null) {
				counterMetricsSum += counter;
			}
		}

		return counterMetricsSum;
	}

	private void sendEntityEvents() {
		for (JSONObject entityEvent : entityEvents) {
			kafkaEventsWriter.send(null, entityEvent.toJSONString(JSONStyle.NO_COMPRESS));
		}

		entityEvents.clear();
	}
}
