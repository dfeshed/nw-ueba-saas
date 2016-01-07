package fortscale.collection.jobs.smart;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.IEntityEventSender;
import fortscale.utils.ConversionUtils;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.MetricsReader;
import fortscale.utils.kafka.ReachSumMetricsDecider;
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
	@Value("${fortscale.samza.aggregation.prevalence.stats.metrics.task}")
	private String counterCreationJob;
	@Value("${fortscale.samza.aggregation.prevalence.stats.metrics.class}")
	private String counterCreationClass;
	@Value("${kafka.entity.event.topic}")
	private String outputTopicName;

	private int batchSize;
	private int checkRetries;
	private List<String> metricsOfEntityEvents;
	private String zookeeper;
	private int port;
	private KafkaEventsWriter kafkaEventsWriter;
	private int batchCounter;
	private long counterMetricsSum;

	public KafkaThrottlerEntityEventSender(int batchSize, int checkRetries) {
		Assert.isTrue(batchSize > 0);
		Assert.isTrue(checkRetries > 0);
		String[] brokerListSplit = brokerList.split(":");

		this.batchSize = batchSize;
		this.checkRetries = checkRetries;
		this.metricsOfEntityEvents = new ArrayList<>();
		this.zookeeper = brokerListSplit[0];
		this.port = Integer.parseInt(brokerListSplit[1]);
		this.kafkaEventsWriter = new KafkaEventsWriter(outputTopicName);

		for (EntityEventConf entityEventConf : entityEventConfService.getEntityEventDefinitions()) {
			metricsOfEntityEvents.add(getCounterName(entityEventConf));
		}

		this.batchCounter = 0;
		this.counterMetricsSum = getCounterMetricsSum();
	}

	@Override
	public void send(JSONObject entityEvent) {
		if (entityEvent == null) {
			return;
		}

		kafkaEventsWriter.send(null, entityEvent.toJSONString(JSONStyle.NO_COMPRESS));
		batchCounter++;

		if (batchCounter == batchSize) {
			ReachSumMetricsDecider decider = new ReachSumMetricsDecider(
					metricsOfEntityEvents, counterMetricsSum + batchSize);
			boolean result = MetricsReader.waitForMetrics(
					zookeeper, port, counterCreationClass, counterCreationJob,
					decider, MILLISECONDS_TO_WAIT, checkRetries);

			if (!result) {
				String errorMsg = "Waiting for processing of entity events timed out.";
				logger.error(errorMsg);
				throw new RuntimeException(errorMsg);
			} else {
				counterMetricsSum += batchSize;
				batchCounter = 0;
			}
		}
	}

	private String getCounterName(EntityEventConf entityEventConf) {
		return String.format("%s.%s%s", eventType, entityEventConf.getName(), counterNameSuffix);
	}

	private long getCounterMetricsSum() {
		long counterMetricsSum = 0;

		CaptorMetricsDecider captor = new CaptorMetricsDecider(metricsOfEntityEvents);
		long offset = 0;

		MetricsReader.MetricsResults metricsResults = new MetricsReader.MetricsResults(false,0,null);
		do {
			offset = metricsResults.getOffset();
			metricsResults = MetricsReader.fetchMetric(offset, zookeeper, port, counterCreationClass, counterCreationJob, captor);

			if(metricsResults.isFound()) {
				for (Object capturedMetric : captor.getCapturedMetricsMap().values()) {
					Long counter = ConversionUtils.convertToLong(capturedMetric);
					if (counter != null) {
						counterMetricsSum += counter;
					}
				}
			}
		}while(metricsResults != null && metricsResults.getOffset() > offset);

		return counterMetricsSum;
	}
}
