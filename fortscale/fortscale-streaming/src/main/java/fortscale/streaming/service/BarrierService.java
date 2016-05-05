package fortscale.streaming.service;

import java.util.List;

import net.minidev.json.JSONObject;

import org.apache.samza.storage.kv.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.streaming.UserTimeBarrier;
import fortscale.streaming.exceptions.KeyValueDBException;

/**
 * Barrier for events that holds a latest time stamp and discriminating fields 
 * for every processed event in a samza store.
 */
public class BarrierService {

	private static Logger logger = LoggerFactory.getLogger(BarrierService.class);
	
	private KeyValueStore<String, UserTimeBarrier> store;
	private List<String> discriminatorsFields;
	
	public BarrierService(KeyValueStore<String, UserTimeBarrier> store, List<String> discriminatorsFields) {
		this.store = store;
		this.discriminatorsFields = discriminatorsFields;
	}
	
	public boolean isEventAfterBarrier(String username, long timestamp, JSONObject message) {
		// get the barrier from the state, stored by table name
		UserTimeBarrier barrier = store.get(username);
		if (barrier == null)
			return true;

		String discriminator = UserTimeBarrier.calculateDiscriminator(message, discriminatorsFields);
		return barrier.isEventAfterBarrier(timestamp, discriminator);
	}
	
	
	public void flushBarrier() throws KeyValueDBException {
		try {
			store.flush();
		} catch (Exception exception) {
			logger.error("error flushing barrier", exception);
			throw new KeyValueDBException("error while trying to do store flush", exception);
		}
	}
	
	public void updateBarrier(String username, long timestamp, JSONObject message) throws KeyValueDBException {
		if (username == null)
			return;
		
		// get the barrier model to update
		UserTimeBarrier barrier = store.get(username);
		if (barrier == null)
			barrier = new UserTimeBarrier();

		// update barrier in case it is not too much in the future
		String discriminator = UserTimeBarrier.calculateDiscriminator(message, discriminatorsFields);
		boolean updated = barrier.updateBarrier(timestamp, discriminator);
		if (updated)
			saveBarrierForUser(username, barrier);
	}
	
	
	protected void saveBarrierForUser(String username, UserTimeBarrier barrier) throws KeyValueDBException {
		try {
			store.put(username, barrier);
		} catch (Exception exception) {
			logger.error(String.format("error storing barrier value for username: %s", username), exception);
			throw new KeyValueDBException(String.format("error while trying to store user barrier %s", username), exception);
		}
	}
}
