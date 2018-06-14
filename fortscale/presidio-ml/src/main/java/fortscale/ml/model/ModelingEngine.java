package fortscale.ml.model;

import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.metrics.ModelingServiceMetricsContainer;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.logging.Logger;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * A {@link ModelingEngine} corresponds to a specific {@link ModelConf}.
 * When the {@link #process(String, Instant, StoreMetadataProperties)} method is called,
 * the engine selects the context IDs, retrieves their data, builds and stores their models.
 *
 * @author Lior Govrin
 */
public class ModelingEngine {
	private static final Logger logger = Logger.getLogger(ModelingEngine.class);

	private ModelConf modelConf;
	private IContextSelector contextSelector;
	private AbstractDataRetriever dataRetriever;
	private IModelBuilder modelBuilder;
	private ModelStore modelStore;
	private long timeRangeInSeconds;
	private ModelingServiceMetricsContainer modelingServiceMetricsContainer;
	private long allContextsForcedSelectionIntervalInSeconds;

	public ModelingEngine(
			ModelConf modelConf,
			IContextSelector contextSelector,
			AbstractDataRetriever dataRetriever,
			IModelBuilder modelBuilder,
			ModelStore modelStore,
			ModelingServiceMetricsContainer modelingServiceMetricsContainer,
			Duration allContextsForcedSelectionInterval) {

		this.modelConf = modelConf;
		this.contextSelector = contextSelector;
		this.dataRetriever = dataRetriever;
		this.modelBuilder = modelBuilder;
		this.modelStore = modelStore;
		this.timeRangeInSeconds = modelConf.getDataRetrieverConf().getTimeRangeInSeconds();
		this.modelingServiceMetricsContainer = modelingServiceMetricsContainer;
		this.allContextsForcedSelectionIntervalInSeconds = allContextsForcedSelectionInterval.getSeconds();
	}

	/**
	 * Run this {@link ModelingEngine} with the given input.
	 *
	 * @param sessionId  the session ID of the built models
	 * @param endInstant the end time of the built models
	 */
	public void process(String sessionId, Instant endInstant, StoreMetadataProperties storeMetadataProperties) {
		logger.info("Process: modelConf {}, sessionId {}, endInstant {}.", modelConf.getName(), sessionId, endInstant);
		Set<String> contextIds = getContextIds(sessionId, endInstant);

		long numOfSuccesses = 0;
		long numOfFailures = 0;

		Set<String> factoryNames = new HashSet<>();
		factoryNames.add(modelConf.getModelBuilderConf().getFactoryName());
		factoryNames.add(modelConf.getDataRetrieverConf().getFactoryName());
		modelingServiceMetricsContainer.init(factoryNames, endInstant, contextIds.size());

		for (String contextId : contextIds) {
			boolean success = process(sessionId, endInstant, contextId, storeMetadataProperties);
			if (success) numOfSuccesses++;
			else numOfFailures++;
		}

		modelingServiceMetricsContainer.updateMetric(endInstant, numOfSuccesses, numOfFailures);

		logger.info("Process finished: {} successes, {} failures.", numOfSuccesses, numOfFailures);
	}

	/*
	 * Run the selector step. If this is not a global model, the engine checks with the store what is the previous end
	 * instant across all models that have the given session ID. Then the selector uses the previous end instant as the
	 * starting point when looking for distinct context IDs. If there are no models in the store with the given session
	 * ID and a previous end instant, the starting point is the given end instant minus the time range configured in the
	 * data retriever configuration. Context IDs that already have a model with the given session ID and end instant are
	 * filtered out. If this is a global model, a singleton containing null is returned.
	 */
	private Set<String> getContextIds(String sessionId, Instant endInstant) {
		Set<String> contextIds;

		if (contextSelector != null) {
			Instant startInstant;

			if (allContextsForcedSelectionIntervalInSeconds > 0 &&
				endInstant.getEpochSecond() % allContextsForcedSelectionIntervalInSeconds == 0) {
				// Once every configured interval (e.g. once a week), select all the contexts that were active
				// in the models' time span, and not just the contexts that were active since the previous run
				startInstant = endInstant.minusSeconds(timeRangeInSeconds);
			} else {
				Instant prevEndInstant = modelStore.getLatestEndInstantLt(modelConf, sessionId, endInstant);
				startInstant = prevEndInstant == null ? endInstant.minusSeconds(timeRangeInSeconds) : prevEndInstant;
			}

			contextIds = contextSelector.getContexts(new TimeRange(startInstant, endInstant));
			logger.info("Contextual model: Selected {} context IDs.", contextIds.size());
		} else {
			contextIds = new HashSet<>();
			contextIds.add(null);
			logger.info("Global model: Selected 1 context ID (null).");
		}

		List<String> contextIdsWithModels = modelStore.getContextIdsWithModels(modelConf, sessionId, endInstant);
		contextIds.removeAll(contextIdsWithModels);
		logger.info("Filtered out {} context IDs that already have a model with session ID {} and end instant {}.",
				contextIdsWithModels.size(), sessionId, endInstant);
		return contextIds;
	}

	/*
	 * Run the retriever, builder and store steps for the given context ID.
	 */
	private boolean process(String sessionId, Instant endInstant, String contextId, StoreMetadataProperties storeMetadataProperties) {
		ModelBuilderData modelBuilderData;
		Model model;

		// Retriever
		try {
			modelBuilderData = dataRetriever.retrieve(contextId, Date.from(endInstant));
			modelingServiceMetricsContainer.updateMetric(endInstant, modelBuilderData.getNoDataReason());

		} catch (Exception e) {
			logger.error("Failed to retrieve data for context ID {}.", contextId, e);
			return false;
		}

		if (!modelBuilderData.dataExists()) {
			return noDataReasonToBoolean(modelBuilderData, contextId);
		}

		// Builder
		try {
			model = modelBuilder.build(modelBuilderData.getData());
		} catch (Exception e) {
			logger.error("Failed to build model for context ID {}.", contextId, e);
			return false;
		}

		if (model == null) {
			logger.error("Built model for context ID {} is null.", contextId);
			return false;
		}

		// Store
		try {
			TimeRange timeRange = new TimeRange(endInstant.minusSeconds(timeRangeInSeconds), endInstant);
			modelStore.save(modelConf, sessionId, contextId, model, timeRange, storeMetadataProperties);
		} catch (Exception e) {
			logger.error("Failed to store model for context ID {}.", contextId, e);
			return false;
		}

		logger.debug("Retriever, builder and store steps successfully finished for context ID {}.", contextId);
		return true;
	}

	private boolean noDataReasonToBoolean(ModelBuilderData modelBuilderData, String contextId) {
		NoDataReason noDataReason = modelBuilderData.getNoDataReason();

		switch (noDataReason) {
			case ALL_DATA_FILTERED:
				// TODO: Add metric
				return true;
			case NO_DATA_IN_DATABASE:
				if (contextId == null) {
					/*
					 * If the context ID is null, then it's a global model, and there isn't a selector.
					 * Therefore it is possible that there's simply no data for this model in the database.
					 */
					// TODO: Add metric
					return true;
				} else {
					/*
					 * If the context ID is not null, then it's not a global model, and there is a selector.
					 * If there's no data in the database, then the selector and the retriever are inconsistent.
					 */
					// TODO: Add metric
					logger.error("No data in database for context ID {}.", contextId);
					return false;
				}
			default:
				String s = String.format("Unsupported %s %s.", NoDataReason.class.getSimpleName(), noDataReason);
				throw new IllegalArgumentException(s);
		}
	}
}
