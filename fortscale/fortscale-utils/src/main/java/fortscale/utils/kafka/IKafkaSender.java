package fortscale.utils.kafka;

/**
 * Created by tomerd on 31/12/2015.
 */
public interface IKafkaSender {
	void flushMessages() throws Exception;
	void shutDown();
}
