package fortscale.utils.kafka;

/**
 * Created by tomerd on 31/12/2015.
 */
public class KafkaMessage {

	private String messageString;
	private long epochTime;

	public KafkaMessage(String messageString, long epochTime) {
		this.messageString = messageString;
		this.epochTime = epochTime;
	}

	public String getMessageString() {
		return messageString;
	}

	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}

	public long getEpochTime() {
		return epochTime;
	}

	public void setEpochTime(long epochTime) {
		this.epochTime = epochTime;
	}
}
