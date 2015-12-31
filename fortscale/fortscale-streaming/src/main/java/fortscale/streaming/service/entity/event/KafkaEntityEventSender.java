package fortscale.streaming.service.entity.event;

import fortscale.entity.event.IEntityEventSender;
import net.minidev.json.JSONObject;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.util.StringUtils;

public class KafkaEntityEventSender implements IEntityEventSender {
	private static final String KAFKA_SYSTEM = "kafka";

	private String stream;
	private MessageCollector collector;

	/**
	 * @param stream The Kafka topic incoming entity events will be sent to.
	 *               If !StringUtils.hasText(stream), all incoming entity events will be discarded.
	 */
	public KafkaEntityEventSender(String stream) {
		this.stream = stream;
	}

	/**
	 * @param collector The message collector that will be used when sending future incoming entity events.
	 *                  If collector == null, future incoming entity events will be discarded.
	 */
	public void setCollector(MessageCollector collector) {
		this.collector = collector;
	}

	@Override
	public void send(JSONObject entityEvent) {
		if (StringUtils.hasText(stream) && collector != null && entityEvent != null) {
			collector.send(new OutgoingMessageEnvelope(new SystemStream(KAFKA_SYSTEM, stream), entityEvent.toJSONString()));
		}
	}

	@Override
	public void close() {}
}
