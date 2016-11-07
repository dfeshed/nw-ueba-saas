package fortscale.streaming.service.model;

import fortscale.streaming.ConfigUtils;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.utils.logging.Logger;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.util.Assert;

import java.util.Set;

public class ModelBuildingSamzaStore {
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

	public ModelBuildingRegistration getRegistration(String sessionId,
													 String modelConfName,
													 boolean selectHighScoreContexts,
													 Set<String> specifiedContextIds) {
		return modelBuildingStore.get(getKey(sessionId, modelConfName, selectHighScoreContexts, specifiedContextIds));
	}

	public void storeRegistration(ModelBuildingRegistration registration) {
		if (registration != null) {
			String key = getKey(
					registration.getSessionId(),
					registration.getModelConfName(),
					registration.selectHighScoreContexts(),
					registration.getSpecifiedContextIds()
			);
			modelBuildingStore.put(key, registration);
		}
	}

	public void deleteRegistration(String sessionId,
								   String modelConfName,
								   boolean selectHighScoreContexts,
								   Set<String> specifiedContextIds) {
		modelBuildingStore.delete(getKey(sessionId, modelConfName, selectHighScoreContexts, specifiedContextIds));
	}


	private static String getKey(String sessionId,
								 String modelConfName,
								 boolean selectHighScoreContexts,
								 Set<String> specifiedContextIds) {
		return String.format("%s%s%s%b%s", sessionId, KEY_DELIMITER, modelConfName, selectHighScoreContexts, specifiedContextIds);
	}

	public KeyValueIterator<String, ModelBuildingRegistration> getIterator() {
		return modelBuildingStore.all();
	}

	public ModelBuildingRegistration getRegistration(String key) {
		return modelBuildingStore.get(key);
	}
}
