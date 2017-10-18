package fortscale.ml.model;

import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * A {@link ModelingEngine} corresponds to a specific {@link ModelConf}. When the {@link #process(String, Instant)}
 * method is called, the engine selects the context IDs, retrieves their data, builds and stores their models.
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

	public ModelingEngine(
			ModelConf modelConf,
			IContextSelector contextSelector,
			AbstractDataRetriever dataRetriever,
			IModelBuilder modelBuilder,
			ModelStore modelStore) {

		this.modelConf = modelConf;
		this.contextSelector = contextSelector;
		this.dataRetriever = dataRetriever;
		this.modelBuilder = modelBuilder;
		this.modelStore = modelStore;
		this.timeRangeInSeconds = modelConf.getDataRetrieverConf().getTimeRangeInSeconds();
	}

	/**
	 * Run this {@link ModelingEngine} with the given input.
	 *
	 * @param sessionId  the session ID of the built models
	 * @param endInstant the end time of the built models
	 */
	public void process(String sessionId, Instant endInstant) {
		logger.info("Process: modelConf {}, sessionId {}, endInstant {}.", modelConf.getName(), sessionId, endInstant);
		Set<String> contextIds = getContextIds(sessionId, endInstant);

		long numOfSuccesses = 0;
		long numOfFailures = 0;

		for (String contextId : contextIds) {
			boolean success = process(sessionId, endInstant, contextId);
			if (success) numOfSuccesses++;
			else numOfFailures++;
		}

		logger.info("Process finished: {} successes, {} failures.", numOfSuccesses, numOfFailures);
	}

	/*
	 * Run the selector step. If this is not a global model, the engine checks with the store what is the latest end
	 * time across all models that have the given session ID. Then the selector uses the latest end time as the starting
	 * point when looking for distinct context IDs. If there are no models in the store with the given session ID, the
	 * starting point is the given end instant minus the time range configured in the data retriever configuration. If
	 * this is a global model, a singleton containing null is returned.
	 */
	private Set<String> getContextIds(String sessionId, Instant endInstant) {
		if (contextSelector != null) {
			Instant latestModelEndTimeInStore = modelStore.getLatestEndTime(modelConf, sessionId);
			TimeRange timeRange;

			if (latestModelEndTimeInStore == null) {
				timeRange = new TimeRange(endInstant.minusSeconds(timeRangeInSeconds), endInstant);
			} else {
				timeRange = new TimeRange(latestModelEndTimeInStore, endInstant);
			}

			Set<String> contextIds = contextSelector.getContexts(timeRange);
			logger.info("Selected {} context IDs.", contextIds.size());
			return contextIds;
		} else {
			logger.info("Global model: Returning a single context ID, null.");
			return Collections.singleton(null);
		}
	}

	/*
	 * Run the retriever, builder and store steps for the given context ID.
	 */
	private boolean process(String sessionId, Instant endInstant, String contextId) {
		ModelBuilderData modelBuilderData;
		Model model;

		// Retriever
		try {
			modelBuilderData = dataRetriever.retrieve(contextId, Date.from(endInstant));
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
			modelStore.save(modelConf, sessionId, contextId, model, timeRange);
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
