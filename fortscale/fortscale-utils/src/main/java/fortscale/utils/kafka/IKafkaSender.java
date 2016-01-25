package fortscale.utils.kafka;

/**
 * Created by tomerd on 31/12/2015.
 */
public interface IKafkaSender {
	void shutDown();
	void callSynchronizer(long epochTime);
}
