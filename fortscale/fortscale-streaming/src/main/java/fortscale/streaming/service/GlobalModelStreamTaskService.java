package fortscale.streaming.service;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.FieldModelBuilder;
import fortscale.ml.model.prevalance.PrevalanceModel;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;

public class GlobalModelStreamTaskService {
	private static final Logger logger = LoggerFactory.getLogger(GlobalModelStreamTaskService.class);
	private static final Long DEFAULT_SECONDS_BETWEEN_UPDATES = 24L * 60 * 60; // one day

	private Config config;
	private String globalModelName;
	private List<FieldModelProperties> fieldModels;
	private KeyValueStore<String, PrevalanceModel> store;

	public GlobalModelStreamTaskService(Config config, String globalModelName, KeyValueStore<String, PrevalanceModel> store) {
		this.config = config;
		this.globalModelName = globalModelName;
		this.fieldModels = new ArrayList<>();
		this.store = store;

		// Parse global model configuration
		Config subset = config.subset(String.format("fortscale.model.%s.field.model.", globalModelName));
		for (String fieldModelName : getConfigStringList(subset, "names")) {
			String builderClassName = getConfigString(subset, String.format("%s.builder", fieldModelName));
			Long secondsBetweenUpdates = subset.getLong(String.format("%s.seconds.between.updates", fieldModelName), DEFAULT_SECONDS_BETWEEN_UPDATES);

			// Add new global field model
			if (StringUtils.isNotBlank(builderClassName))
				fieldModels.add(new FieldModelProperties(fieldModelName, builderClassName, secondsBetweenUpdates));
		}
	}

	/**
	 * Updates the global prevalence model in the store.
	 */
	public void updateGlobalModel(Long timestamp) {
		// Get builders of field models that need to be updated
		Map<String, FieldModelBuilder> builders = createBuilders(timestamp);

		// Iterate all prevalence models across all contexts and feed builders
		KeyValueIterator<String, PrevalanceModel> iterator = store.all();
		while (iterator.hasNext()) {
			PrevalanceModel prevalanceModel = iterator.next().getValue();
			for (FieldModelBuilder fieldModelBuilder : builders.values())
				fieldModelBuilder.feedBuilder(prevalanceModel);
		}
		iterator.close();

		// Get global prevalence model from store or create a new one
		PrevalanceModel globalModel = store.get(globalModelName + "NO_CONTEXT");
		if (globalModel == null)
			globalModel = new PrevalanceModel(globalModelName);

		for (Map.Entry<String, FieldModelBuilder> entry : builders.entrySet()) {
			// Build the global field model and add it to the prevalence model
			FieldModel fieldModel = entry.getValue().buildModel();
			globalModel.setFieldModel(entry.getKey(), fieldModel);
		}

		// Update store with latest global model
		store.put(globalModelName + "NO_CONTEXT", globalModel);
	}

	/**
	 * Creates builders only for models that need to be updated.
	 * the timestamp with the model's "last update timestamp"
	 * will determine if it's time to update the model.
	 */
	private Map<String, FieldModelBuilder> createBuilders(Long timestamp) {
		Map<String, FieldModelBuilder> builders = new HashMap<>();

		for (FieldModelProperties fieldModel : fieldModels) {
			if (timestamp >= fieldModel.lastUpdateTimestamp + fieldModel.secondsBetweenUpdates) {
				FieldModelBuilder builder = null;

				try {
					// Create a new builder instance
					builder = (FieldModelBuilder)Class.forName(fieldModel.builderClassName).newInstance();
				} catch (Exception e) {
					logger.warn(String.format("Could not create FieldModelBuilder %s", fieldModel.builderClassName), e);
				}

				if (builder != null) {
					// Initialize the builder and update the relevant timestamp
					builder.initBuilder(config, globalModelName, fieldModel.fieldModelName);
					builders.put(fieldModel.fieldModelName, builder);
					fieldModel.lastUpdateTimestamp = timestamp;
				}
			}
		}

		return builders;
	}

	private static final class FieldModelProperties {
		public String fieldModelName;
		public String builderClassName;
		public Long secondsBetweenUpdates;
		public Long lastUpdateTimestamp;

		public FieldModelProperties(String fieldModelName, String builderClassName, Long secondsBetweenUpdates) {
			this.fieldModelName = fieldModelName;
			this.builderClassName = builderClassName;
			this.secondsBetweenUpdates = secondsBetweenUpdates;
			this.lastUpdateTimestamp = 0L;
		}
	}
}
