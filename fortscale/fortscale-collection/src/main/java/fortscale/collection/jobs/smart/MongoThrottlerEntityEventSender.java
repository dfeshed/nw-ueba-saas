package fortscale.collection.jobs.smart;

import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.IEntityEventSender;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configurable(preConstruction = true)
public class MongoThrottlerEntityEventSender implements IEntityEventSender {
	private static final Logger logger = Logger.getLogger(MongoThrottlerEntityEventSender.class);
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
	private String zookeeper;
	private int port;
	private KafkaEventsWriter kafkaEventsWriter;
	private long batchCounter;
	private long totalNumberOfNewEvents;
	private EntityEventCreationThrottler entityEventCreationThrottler;

	public MongoThrottlerEntityEventSender(int batchSize, int checkRetries, long timeToWaitInSeconds) throws TimeoutException {
		Assert.isTrue(batchSize > 0);
		Assert.isTrue(checkRetries > 0);
		String[] brokerListSplit = brokerList.split(":");

		this.batchSize = batchSize;
		this.checkRetries = checkRetries;
		this.zookeeper = brokerListSplit[0];
		this.port = Integer.parseInt(brokerListSplit[1]);
		this.kafkaEventsWriter = new KafkaEventsWriter(outputTopicName);
		this.batchCounter = 0;
		this.totalNumberOfNewEvents = 0;
		this.entityEventCreationThrottler = new EntityEventCreationThrottler(TimeUnit.SECONDS.toMillis(timeToWaitInSeconds));
	}

	public void init(){}

	public void close(){
		try {
			kafkaEventsWriter.close();
		} catch (Exception e){
			logger.error("got an exception while closing the kafka events writer of the entity events", e);
		}
	}

	@Override
	public void send(JSONObject entityEvent) throws TimeoutException {
		if (entityEvent == null) {
			return;
		}

		kafkaEventsWriter.send(null, entityEvent.toJSONString(JSONStyle.NO_COMPRESS));
		batchCounter++;
		totalNumberOfNewEvents++;

		if (batchCounter == batchSize) {
			logger.info("{} messages sent, waiting for scored entity events to be saved in mongo...", batchCounter);
			throttle();
		}
	}

	@Override
	public void throttle() throws TimeoutException {
		entityEventCreationThrottler.throttle(totalNumberOfNewEvents);
		batchCounter = 0;
	}
}

