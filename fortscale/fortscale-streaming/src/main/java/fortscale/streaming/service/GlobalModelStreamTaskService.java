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
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;

public class GlobalModelStreamTaskService {
	private static final Logger logger = LoggerFactory.getLogger(GlobalModelStreamTaskService.class);
	private static final Long DEFAULT_SECONDS_BETWEEN_UPDATES = 24L * 60 * 60; // one day

	ModelService modelService = new ModelServiceImpl();

	private Config config;
	private String globalModelName;
	private String contextConstant;
	private List<FieldModelProperties> fieldModels;
	private KeyValueStore<String, PrevalanceModel> store;

	public GlobalModelStreamTaskService(
			Config config, String globalModelName,
			Map<String, PrevalanceModelStreamingService> map,
			KeyValueStore<String, PrevalanceModel> store) {

		this.config = config;
		this.globalModelName = globalModelName;
		this.fieldModels = new ArrayList<>();
		this.store = store;

		// Get dummy context constant
		contextConstant = config.get(String.format("fortscale.model.%s.context.constant", globalModelName), null);
		Assert.isTrue(StringUtils.isNotBlank(contextConstant), "Missing global context constant");

		// Parse global model configuration
		Config subset = config.subset(String.format("fortscale.model.%s.field.model.", globalModelName));
		for (String fieldModelName : getConfigStringList(subset, "names")) {
			String builderClassName = getConfigString(subset, String.format("%s.builder", fieldModelName));
			Long secondsBetweenUpdates = subset.getLong(String.format("%s.seconds.between.updates", fieldModelName), DEFAULT_SECONDS_BETWEEN_UPDATES);

			// Add new global field model
			if (StringUtils.isNotBlank(builderClassName))
				fieldModels.add(new FieldModelProperties(fieldModelName, builderClassName, secondsBetweenUpdates));
		}

		PrevalanceModelBuilder builder = PrevalanceModelBuilderImpl.createModel(globalModelName, config, null);
		PrevalanceModelStreamingService service = new PrevalanceModelStreamingService(store, builder, 0);
		map.put(globalModelName, service);
	}

	/**
	 * Updates the global prevalence model in the store.
	 */
	public void updateGlobalModel(Long timestamp) {
		// Get builders of field models that need to be updated
		Map<String, FieldModelBuilder> builders = createBuilders(timestamp);
		if (builders.isEmpty())
			return;

		// Iterate all prevalence models across all contexts and feed builders
		KeyValueIterator<String, PrevalanceModel> iterator = store.all();
		while (iterator.hasNext()) {
			PrevalanceModel prevalanceModel = iterator.next().getValue();
			for (FieldModelBuilder fieldModelBuilder : builders.values())
				fieldModelBuilder.feedBuilder(prevalanceModel);
		}
		iterator.close();

		// Get global prevalence model from store/mongo or create a new one
		PrevalanceModel globalModel = store.get(globalModelName + contextConstant);

		if (globalModel == null) {
			try {
				globalModel = modelService.getModel(contextConstant, globalModelName);
			} catch (Exception e) {
				String error = String.format("Exception while trying to get model %s from mongo DB (context is %s)", globalModelName, contextConstant);
				logger.warn(error, e);
				globalModel = null;
			}
		}

		if (globalModel == null) {
			globalModel = new PrevalanceModel(globalModelName);
		}

		for (Map.Entry<String, FieldModelBuilder> entry : builders.entrySet()) {
			// Build the global field model and add it to the prevalence model
			FieldModel fieldModel = entry.getValue().buildModel();
			globalModel.setFieldModel(entry.getKey(), fieldModel);
		}

		// Update store and mongo with latest global model
		store.put(globalModelName + contextConstant, globalModel);
		try {
			modelService.updateModel(contextConstant, globalModel);
		} catch (Exception e) {
			String error = String.format("Exception while trying to put model %s in mongo DB (context is %s)", globalModelName, contextConstant);
			logger.warn(error, e);
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
