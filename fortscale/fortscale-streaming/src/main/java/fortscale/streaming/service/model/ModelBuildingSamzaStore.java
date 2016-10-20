package fortscale.streaming.service.model;

import fortscale.streaming.ConfigUtils;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.utils.logging.Logger;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.util.Assert;

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

	public ModelBuildingRegistration getRegistration(String sessionId, String modelConfName, ModelBuildingExtraParams extraParams) {
		return modelBuildingStore.get(getKey(sessionId, modelConfName, extraParams));
	}

	public void storeRegistration(ModelBuildingRegistration registration) {
		if (registration != null) {
			String key = getKey(registration.getSessionId(), registration.getModelConfName(), registration.getExtraParams());
			modelBuildingStore.put(key, registration);
		}
	}

	public void deleteRegistration(String sessionId, String modelConfName, ModelBuildingExtraParams extraParams) {
		modelBuildingStore.delete(getKey(sessionId, modelConfName, extraParams));
	}


	private static String getKey(String sessionId, String modelConfName, ModelBuildingExtraParams extraParams) {
		return String.format("%s%s%s%d", sessionId, KEY_DELIMITER, modelConfName, extraParams.hashCode());
	}

	public KeyValueIterator<String, ModelBuildingRegistration> getIterator() {
		return modelBuildingStore.all();
	}

	public ModelBuildingRegistration getRegistration(String key) {
		return modelBuildingStore.get(key);
	}
}
