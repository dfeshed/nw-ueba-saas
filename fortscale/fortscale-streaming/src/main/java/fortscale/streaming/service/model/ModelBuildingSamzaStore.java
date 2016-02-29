package fortscale.streaming.service.model;

import fortscale.streaming.ConfigUtils;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.utils.logging.Logger;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.util.Assert;

import java.util.Iterator;

public class ModelBuildingSamzaStore {
	private static final String NULL_VALUE_ERROR_MSG_FORMAT = String.format(
			"{} iterator indicates that the following key is present in the store, "
			.concat("but the getter returns a null value - returning null. ")
			.concat("Key = {}, expected value type = %s."), ModelBuildingRegistration.class.getSimpleName());
	private static final Logger logger = Logger.getLogger(ModelBuildingSamzaStore.class);
	private static final String STORE_NAME_PROPERTY = "fortscale.model.building.store.name";
	private static final String KEY_DELIMITER = "#";

	private KeyValueStore<String, ModelBuildingRegistration> modelBuildingStore;

	@SuppressWarnings("unchecked")
	public ModelBuildingSamzaStore(ExtendedSamzaTaskContext context) {
		Assert.notNull(context);
		String storeName = ConfigUtils.getConfigString(context.getConfig(), STORE_NAME_PROPERTY);
		modelBuildingStore = (KeyValueStore<String, ModelBuildingRegistration>)context.getStore(storeName);
		Assert.notNull(modelBuildingStore);
	}

	public ModelBuildingRegistration getRegistration(String sessionId, String modelConfName) {
		return modelBuildingStore.get(getKey(sessionId, modelConfName));
	}

	public void storeRegistration(ModelBuildingRegistration registration) {
		if (registration != null) {
			String key = getKey(registration.getSessionId(), registration.getModelConfName());
			modelBuildingStore.put(key, registration);
		}
	}

	public void deleteRegistration(String sessionId, String modelConfName) {
		modelBuildingStore.delete(getKey(sessionId, modelConfName));
	}

	public Iterator<ModelBuildingRegistration> getRegistrationsIterator() {
		return new RegistrationsIterator();
	}

	private static String getKey(String sessionId, String modelConfName) {
		return String.format("%s%s%s", sessionId, KEY_DELIMITER, modelConfName);
	}

	private final class RegistrationsIterator implements Iterator<ModelBuildingRegistration> {
		private KeyValueIterator<String, ModelBuildingRegistration> keyValueIterator;

		public RegistrationsIterator() {
			keyValueIterator = modelBuildingStore.all();
		}

		@Override
		public boolean hasNext() {
			boolean hasNext = keyValueIterator.hasNext();
			if (!hasNext) keyValueIterator.close();
			return hasNext;
		}

		@Override
		public ModelBuildingRegistration next() {
			String key = keyValueIterator.next().getKey();
			ModelBuildingRegistration value = modelBuildingStore.get(key);

			if (value == null) {
				logger.error(NULL_VALUE_ERROR_MSG_FORMAT, modelBuildingStore.getClass().getSimpleName(), key);
			}

			return value;
		}

		@Override
		public void remove() {
			// Removing registrations is not supported
		}
	}
}
