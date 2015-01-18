package fortscale.utils.kafka;


/**
 * Short main class for sending messages to kafka from command line (kafka-console-producer.sh has some issues)
 * Date 1/18/2015.
 */
public class ConsoleSendMessageToKafka {

	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("Needs 3 parameters: topic key message");
			return;
		}
		String streamingTopic = args[0];
		String key = args[1];
		String message = args[2];

		System.out.println("Creating writer for topic " + streamingTopic);
		KafkaEventsWriter streamWriter = new ConsoleKafkaEventsWriter(streamingTopic);

		System.out.println("Sending message with key " + key + " and message " + message);
		streamWriter.send(key, message);

		System.out.println("Closing writer");
		streamWriter.close();



	}

	/**
	 * Create writer for using in command line - without sprung context
	 */
	public static class ConsoleKafkaEventsWriter extends KafkaEventsWriter {

		public ConsoleKafkaEventsWriter(String topic) {
			super(topic);
			this.kafkaBrokerList = "localhost:9092";
		}
	}

}
