package presidio.ade.domain.store.aggr.smart;

import fortscale.utils.pagination.ContextIdToNumOfItems;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.List;
import java.util.Set;

/**
 * Smart data reader
 */
public interface SmartRecordDataReader {

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
    List<SmartRecord> readRecords(SmartRecordsMetadata smartRecordsMetadata, Set<String> contextIds, int numOfItemsToSkip, int numOfItemsToRead);

    /**
     * Validate that the contextId indexed in the store.
     */
    void ensureContextIdIndex(String configurationName);
}
