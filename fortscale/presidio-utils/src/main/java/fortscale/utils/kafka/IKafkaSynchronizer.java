package fortscale.utils.kafka;

import java.util.concurrent.TimeoutException;

/**
 * Interface for synchronizing Kafka topics
 * Created by tomerd on 31/12/2015.
 */
public interface IKafkaSynchronizer {
	boolean synchronize(long syncParam) throws TimeoutException;
}
