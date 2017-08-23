package presidio.ade.domain.store.smart;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.List;
import java.util.Set;

/**
 * Exposes the methods that read from the {@link SmartRecord}s store.
 *
 * @author Lior Govrin
 */
public interface SmartDataReader {

	/**
	 * Aggregate data to map of context ids and total num of events.
	 *
	 * @param smartRecordsMetadata    describing the records (configuration name, start instant, end instant)
	 * @return list of ContextIdToNumOfItems
	 */
	List<ContextIdToNumOfItems> aggregateContextIdToNumOfEvents(SmartRecordsMetadata smartRecordsMetadata);

	/**
	 * Read data.
	 * numOfItemsToSkip and numOfItemsToRead used in order to read data in paging.
	 *
	 * @param smartRecordsMetadata  metadata e.g: configuration name, start instant, end instant
	 * @param contextIds       set of context ids
	 * @param numOfItemsToSkip num of items to skip
	 * @param numOfItemsToRead num of items to read
	 * @return list of Smart records
	 */
	List<SmartRecord> readRecords(SmartRecordsMetadata smartRecordsMetadata, Set<String> contextIds, int numOfItemsToSkip, int numOfItemsToRead, int scoreThreshold);

	/**
	 * Validate that the contextId indexed in the store.
	 */
	void ensureContextIdIndex(String configurationName);

	/**
	 *
	 * @return smart collection names
	 */
	Set<String> getAllSmartConfigurationNames();




}
