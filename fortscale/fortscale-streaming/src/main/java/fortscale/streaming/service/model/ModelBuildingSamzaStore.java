package fortscale.streaming.service.model;

import fortscale.streaming.ConfigUtils;
import fortscale.streaming.ExtendedSamzaTaskContext;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.util.Assert;

import java.util.Iterator;

public class ModelBuildingSamzaStore {
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
			return keyValueIterator.next().getValue();
		}

		@Override
		public void remove() {
			// Removing registrations is not supported
		}
	}
}
