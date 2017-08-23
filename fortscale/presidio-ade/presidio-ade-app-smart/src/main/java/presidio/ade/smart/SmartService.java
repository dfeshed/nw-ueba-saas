package presidio.ade.smart;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.smart.SmartRecordAggregator;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.aggregated.AggregatedDataPaginationParam;
import presidio.ade.domain.pagination.aggregated.AggregatedDataReader;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataStore;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static fortscale.utils.fixedduration.FixedDurationStrategyUtils.splitTimeRangeByStrategy;
import static presidio.ade.domain.record.aggregated.AggregatedFeatureType.fromCodeRepresentation;

/**
 * This service creates {@link SmartRecord}s as follows:
 * 1. Splits the time range into partitions according to the fixed duration strategy.
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
	private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	private final AggregatedDataReader aggregatedDataReader;
	private final SmartScoringService smartScoringService;
	private final SmartDataStore smartDataStore;

	/**
	 * C'tor.
	 *
	 * @param smartRecordConfService             contains all the {@link SmartRecordConf}s
	 * @param aggregatedFeatureEventsConfService contains all the {@link AggregatedFeatureEventConf}s
	 * @param aggregatedDataReader               reads from the store of {@link AdeAggregationRecord}s
	 * @param smartScoringService                scores {@link SmartRecord}s
	 * @param smartDataStore                     the store of {@link SmartRecord}s
	 */
	public SmartService(
			SmartRecordConfService smartRecordConfService,
			AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
			AggregatedDataReader aggregatedDataReader,
			SmartScoringService smartScoringService,
			SmartDataStore smartDataStore) {

		this.smartRecordConfService = smartRecordConfService;
		this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
		this.aggregatedDataReader = aggregatedDataReader;
		this.smartScoringService = smartScoringService;
		this.smartDataStore = smartDataStore;
	}

	/**
	 * @param smartRecordConfName   the name of the configuration according to which smart records are created
	 * @param timeRange             the time range for which smart records are created
	 * @param fixedDurationStrategy the fixed duration strategy according to which the time range is split
	 */
	public void process(String smartRecordConfName, TimeRange timeRange, FixedDurationStrategy fixedDurationStrategy) {
		logger.info("Smart service process: {}, {}, {}.", smartRecordConfName, timeRange, fixedDurationStrategy);
		SmartRecordConf conf = smartRecordConfService.getSmartRecordConf(smartRecordConfName);
		Set<AggregatedDataPaginationParam> params = getAggregatedDataPaginationParams(conf);

		for (TimeRange partition : splitTimeRangeByStrategy(timeRange, fixedDurationStrategy)) {
			logger.info("Starting to process time range partition {}.", partition);

			for (PageIterator<AdeAggregationRecord> iterator : aggregatedDataReader.read(params, partition)) {
				SmartRecordAggregator aggregator = new SmartRecordAggregator(conf, fixedDurationStrategy, partition);
				while (iterator.hasNext()) aggregator.updateSmartRecords(iterator.next());
				Collection<SmartRecord> records = aggregator.getSmartRecords();
				smartScoringService.score(records);
				smartDataStore.storeSmartRecords(smartRecordConfName, records);
			}
		}
	}

	private Set<AggregatedDataPaginationParam> getAggregatedDataPaginationParams(SmartRecordConf smartRecordConf) {
		return smartRecordConf.getAggregationRecordNames().stream()
				.map(name -> {
					String type = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(name).getType();
					return new AggregatedDataPaginationParam(name, fromCodeRepresentation(type));
				})
				.collect(Collectors.toSet());
	}
}
