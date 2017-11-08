package presidio.ade.domain.store.enriched;

import fortscale.utils.pagination.ContextIdToNumOfItems;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.util.List;
import java.util.Set;

/**
 * Enriched data reader
 */
public interface EnrichedDataReader {

    /**
     * Aggregate data to map of context ids and total num of events.
     *
     * @param recordsMetadata describing the records (which data source, etc.)
     * @param contextType     type of context (e.g: NORMALIZED_USERNAME_FIELD, NORMALIZED_SRC_MACHINE_FIELD etc.)
     * @return list of ContextIdToNumOfItems
     */
    List<ContextIdToNumOfItems> aggregateContextToNumOfEvents(EnrichedRecordsMetadata recordsMetadata, String contextType);

    /**
     * Read data.
     * numOfItemsToSkip and numOfItemsToRead used in order to read data in paging.
     *
     * @param recordsMetadata  metadata e.g: data source
     * @param contextIds       set of context ids
     * @param contextType      context type (e.g:NORMALIZED_USERNAME_FIELD, NORMALIZED_SRC_MACHINE_FIELD)
     * @param numOfItemsToSkip num of items to skip
     * @param numOfItemsToRead num of items to read
     * @return list of EnrichedRecord
     */
    <U extends EnrichedRecord> List<U> readRecords(EnrichedRecordsMetadata recordsMetadata, Set<String> contextIds, String contextType, int numOfItemsToSkip, int numOfItemsToRead);


    /**
     * Read ordered data.
     * Sort the data by fieldNameToSortBy
     * numOfItemsToSkip and numOfItemsToRead used in order to read data in paging.
     *
     * @param recordsMetadata  metadata e.g: data source
     * @param contextIds       set of context ids
     * @param contextType      context type (e.g:NORMALIZED_USERNAME_FIELD, NORMALIZED_SRC_MACHINE_FIELD)
     * @param numOfItemsToSkip num of items to skip
     * @param numOfItemsToRead num of items to read
     * @param fieldNameToSortBy field name to sort by
     * @return list of EnrichedRecord
     */
    <U extends EnrichedRecord> List<U> readSortedRecords(EnrichedRecordsMetadata recordsMetadata, Set<String> contextIds, String contextType, int numOfItemsToSkip, int numOfItemsToRead, String fieldNameToSortBy);

    /**
     * Validate that the query fields indexed in the store.
     */
    void ensureContextAndDateTimeIndex(String dataSource, String contextType);

    long countRecords(EnrichedRecordsMetadata recordsMetadata, String contextType, String contextId);
}
