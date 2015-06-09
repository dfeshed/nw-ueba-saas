package fortscale.streaming.service;

import fortscale.ml.model.prevalance.*;
import fortscale.ml.service.ModelService;
import fortscale.ml.service.impl.ModelServiceImpl;
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
	public static final String GLOBAL_MODEL_NAME = "globalModelName";
	public static final String GLOBAL_CONTEXT_CONSTANT = "globalContextConstant";

	private static final Logger logger = LoggerFactory.getLogger(GlobalModelStreamTaskService.class);
	private static final Long DEFAULT_SECONDS_BETWEEN_UPDATES = 24L * 60 * 60; // one day

	private Config config;
	private List<FieldModelProperties> fieldModels;
	private KeyValueStore<String, PrevalanceModel> store;
	private ModelService modelService = new ModelServiceImpl();
	private Long minNextUpdateTimestamp = 0L;

	public GlobalModelStreamTaskService(Config config, KeyValueStore<String, PrevalanceModel> store) {
		this.config = config;
		this.fieldModels = new ArrayList<>();
		this.store = store;

		// Parse global model configuration
		Config subset = config.subset("fortscale.model.global.field.model.");
		for (String fieldModelName : getConfigStringList(subset, "names")) {
			String builderClassName = getConfigString(subset, String.format("%s.builder", fieldModelName));
			Long secondsBetweenUpdates = subset.getLong(String.format("%s.seconds.between.updates", fieldModelName), DEFAULT_SECONDS_BETWEEN_UPDATES);

			// Add new global field model
			if (StringUtils.isNotBlank(builderClassName))
				fieldModels.add(new FieldModelProperties(fieldModelName, builderClassName, secondsBetweenUpdates));
		}
	}

	/**
	 * Returns the streaming service for the global prevalence model.
	 * Since the global model scorer needs access to the global model,
	 * and this can only be done using a Model Service that holds a service for each prevalence model,
	 * the global prevalence model must have a service of its own as well.
	 */
	public PrevalanceModelStreamingService getGlobalModelStreamingService() {
		PrevalanceModelBuilder builder = PrevalanceModelBuilderImpl.createModel(GLOBAL_MODEL_NAME, config, null);
		return new PrevalanceModelStreamingService(store, builder, 0);
	}

	/**
	 * Updates the global prevalence model in the store.
	 */
	public void updateGlobalModel(Long timestamp) {
		if (timestamp < minNextUpdateTimestamp)
			return;

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

		// Get global prevalence model from store/mongo or create a new one
		String modelContextConcat = String.format("%s%s", GLOBAL_MODEL_NAME, GLOBAL_CONTEXT_CONSTANT);
		PrevalanceModel globalModel = store.get(modelContextConcat);

		if (globalModel == null) {
			try {
				globalModel = modelService.getModel(GLOBAL_CONTEXT_CONSTANT, GLOBAL_MODEL_NAME);
			} catch (Exception e) {
				logger.warn("Exception while trying to get global model from mongo DB", e);
				globalModel = null;
			}
		}

		if (globalModel == null) {
			globalModel = new PrevalanceModel(GLOBAL_MODEL_NAME);
		}

		for (Map.Entry<String, FieldModelBuilder> entry : builders.entrySet()) {
			// Build the global field model and add it to the prevalence model
			FieldModel fieldModel = entry.getValue().buildModel();
			globalModel.setFieldModel(entry.getKey(), fieldModel);
		}

		// Update store and mongo with latest global model
		store.put(modelContextConcat, globalModel);
		try {
			modelService.updateModel(GLOBAL_CONTEXT_CONSTANT, globalModel);
		} catch (Exception e) {
			logger.warn("Exception while trying to put global model in mongo DB", e);
		}
	}

	/**
	 * Creates builders only for models that need to be updated.
	 * the timestamp with the model's "last update timestamp"
	 * will determine if it's time to update the model.
	 */
	private Map<String, FieldModelBuilder> createBuilders(Long timestamp) {
		Map<String, FieldModelBuilder> builders = new HashMap<>();

		for (FieldModelProperties fieldModel : fieldModels) {
			Long nextUpdateTimestamp = fieldModel.lastUpdateTimestamp + fieldModel.secondsBetweenUpdates;

			if (timestamp >= nextUpdateTimestamp) {
				FieldModelBuilder builder = null;

				try {
					// Create a new builder instance
					builder = (FieldModelBuilder)Class.forName(fieldModel.builderClassName).newInstance();
				} catch (Exception e) {
					logger.warn(String.format("Could not create FieldModelBuilder %s", fieldModel.builderClassName), e);
				}

				if (builder != null) {
					// Initialize the builder and update the relevant timestamp
					builder.initBuilder(config, fieldModel.fieldModelName);
					builders.put(fieldModel.fieldModelName, builder);
					fieldModel.lastUpdateTimestamp = timestamp;

					// Update minimal next update timestamp
					nextUpdateTimestamp = timestamp + fieldModel.secondsBetweenUpdates;
					if (minNextUpdateTimestamp == 0)
						minNextUpdateTimestamp = nextUpdateTimestamp;
					else
						minNextUpdateTimestamp = Math.min(minNextUpdateTimestamp, nextUpdateTimestamp);
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
