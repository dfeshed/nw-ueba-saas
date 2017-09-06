package presidio.ade.smart;

import fortscale.smart.SmartRecordAggregator;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import presidio.ade.domain.pagination.aggregated.AggregatedDataPaginationParam;
import presidio.ade.domain.pagination.aggregated.AggregatedDataReader;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataStore;

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

	private final SmartRecordConfService smartRecordConfService;
	private final Double aggregationRecordsThreshold;
	private final AggregatedDataReader aggregatedDataReader;
	private final SmartScoringService smartScoringService;
	private final SmartDataStore smartDataStore;
	private final TtlService ttlService;

	/**
	 * C'tor.
	 *
	 * @param smartRecordConfService      contains all the {@link SmartRecordConf}s
	 * @param aggregationRecordsThreshold only {@link AdeAggregationRecord}s whose values / scores are larger
	 *                                    than this threshold will be included in the {@link SmartRecord}s
	 * @param aggregatedDataReader        reads from the store of {@link AdeAggregationRecord}s
	 * @param smartScoringService         scores {@link SmartRecord}s
	 * @param smartDataStore              the store of {@link SmartRecord}s
	 */
	public SmartService(
			SmartRecordConfService smartRecordConfService,
			Double aggregationRecordsThreshold,
			AggregatedDataReader aggregatedDataReader,
			SmartScoringService smartScoringService,
			SmartDataStore smartDataStore,
			TtlService ttlService) {

		this.smartRecordConfService = smartRecordConfService;
		this.aggregationRecordsThreshold = aggregationRecordsThreshold;
		this.aggregatedDataReader = aggregatedDataReader;
		this.smartScoringService = smartScoringService;
		this.smartDataStore = smartDataStore;
		this.ttlService = ttlService;
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

		for (TimeRange partition : FixedDurationStrategyUtils.splitTimeRangeByStrategy(timeRange, strategy)) {
			logger.info("Starting to process time range partition {}.", partition);

			aggregatedDataReader.read(params, partition, aggregationRecordsThreshold).forEach(iterator -> {
				SmartRecordAggregator aggregator = new SmartRecordAggregator(conf, strategy, partition);
				while (iterator.hasNext()) aggregator.updateSmartRecords(iterator.next());
				Collection<SmartRecord> records = aggregator.getSmartRecords();
				smartScoringService.score(records);
				smartDataStore.storeSmartRecords(smartRecordConfName, records);
			});
			ttlService.cleanupCollections(timeRange.getStart());
		}
	}
}
