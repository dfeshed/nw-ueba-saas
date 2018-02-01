package presidio.ade.smart;

import fortscale.smart.SmartRecordAggregator;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.aggregated.AggregatedDataPaginationParam;
import presidio.ade.domain.pagination.aggregated.AggregatedDataReader;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.monitoring.flush.MetricContainerFlusher;

import java.util.Collection;
import java.util.Set;

/**
 * This service creates {@link SmartRecord}s as follows:
 * 1. Splits the time range into partitions according to the fixed duration strategy in the {@link SmartRecordConf}.
 * 2. For each partition, creates aggregation record page iterators - One iterator per a closed group of context IDs.
 * 3. For each iterator and context ID in the group, inserts all the aggregation records into a new smart record.
 * 4. Scores all the newly created smart records of the context IDs in the group.
 * 5. Stores the scored smart records in the database.
 *
 * @author Lior Govrin
 */
public class SmartService {
	private static final Logger logger = Logger.getLogger(SmartService.class);
	private static final String CONFIGURATION_NAME = "configuration_name";

	private final SmartRecordConfService smartRecordConfService;
	private final Double aggregationRecordsThreshold;
	private final AggregatedDataReader aggregatedDataReader;
	private final SmartScoringService smartScoringService;
	private final SmartDataStore smartDataStore;
	private final StoreManager storeManager;
	private final MetricContainerFlusher metricContainerFlusher;

	/**
	 * C'tor.
     * @param smartRecordConfService      contains all the {@link SmartRecordConf}s
     * @param aggregationRecordsThreshold only {@link AdeAggregationRecord}s whose values / scores are larger
     *                                    than this threshold will be included in the {@link SmartRecord}s
     * @param aggregatedDataReader        reads from the store of {@link AdeAggregationRecord}s
     * @param smartScoringService         scores {@link SmartRecord}s
     * @param smartDataStore              the store of {@link SmartRecord}s
     * @param metricContainerFlusher
     */
	public SmartService(
            SmartRecordConfService smartRecordConfService,
            Double aggregationRecordsThreshold,
            AggregatedDataReader aggregatedDataReader,
            SmartScoringService smartScoringService,
            SmartDataStore smartDataStore,
            StoreManager storeManager, MetricContainerFlusher metricContainerFlusher) {

		this.smartRecordConfService = smartRecordConfService;
		this.aggregationRecordsThreshold = aggregationRecordsThreshold;
		this.aggregatedDataReader = aggregatedDataReader;
		this.smartScoringService = smartScoringService;
		this.smartDataStore = smartDataStore;
		this.storeManager = storeManager;
		this.metricContainerFlusher = metricContainerFlusher;
	}

	/**
	 * @param smartRecordConfName the name of the configuration according to which smart records are created
	 * @param timeRange           the time range for which smart records are created
	 */
	public void process(String smartRecordConfName, TimeRange timeRange) {
		logger.info("Smart service process: {}, {}.", smartRecordConfName, timeRange);
		SmartRecordConf conf = smartRecordConfService.getSmartRecordConf(smartRecordConfName);
		FixedDurationStrategy strategy = conf.getFixedDurationStrategy();
		Set<AggregatedDataPaginationParam> params = smartRecordConfService.getPaginationParams(smartRecordConfName);

		StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(smartRecordConfName);

		for (TimeRange partition : FixedDurationStrategyUtils.splitTimeRangeByStrategy(timeRange, strategy)) {
			try {

				//Once modelCacheManager save model to cache it will never updating the cache again with newer model.
				//Reset cache required in order to get newer models each partition and not use older models.
				// If this line will be deleted the model cache will need to have some efficient refresh mechanism.
				smartScoringService.resetModelCache();


				logger.info("Starting to process time range partition {}.", partition);
				aggregatedDataReader.read(params, partition).forEach(iterator -> {
					SmartRecordAggregator aggregator = new SmartRecordAggregator(
							conf, strategy, partition, aggregationRecordsThreshold);
					while (iterator.hasNext()) aggregator.updateSmartRecords(iterator.next());
					Collection<SmartRecord> records = aggregator.getSmartRecords();
					smartScoringService.score(records,timeRange);
					smartDataStore.storeSmartRecords(smartRecordConfName, records, storeMetadataProperties);
				});

				//Flush stored metrics to elasticsearch
				metricContainerFlusher.flush();
			}
			catch (Exception e)
			{
				logger.error("got exception while calculating time range={}",timeRange,e);
				throw e;
			}

		}

		storeManager.cleanupCollections(storeMetadataProperties, timeRange.getStart());
	}


	public void cleanup(String smartRecordConfName, TimeRange timeRange) throws Exception {
		StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(smartRecordConfName);
		storeManager.cleanupCollections(storeMetadataProperties, timeRange.getStart(), timeRange.getEnd());
	}


	private StoreMetadataProperties createStoreMetadataProperties(String smartRecordConfName){
		StoreMetadataProperties storeMetadataProperties = new StoreMetadataProperties();
		storeMetadataProperties.setProperty(CONFIGURATION_NAME, smartRecordConfName);
		return storeMetadataProperties;
	}
}
