package fortscale.utils.kafka;

import java.util.concurrent.TimeoutException;

/**
 * Created by tomerd on 31/12/2015.
 */
public interface IKafkaSender {
	void shutDown();
	void callSynchronizer(long syncParam) throws TimeoutException;
}
