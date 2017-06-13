package presidio.ade.domain.pagination.enriched;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.pagination.PaginationService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.impl.EnrichedRecordPageIterator;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreImplMongo;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.util.Map;
import java.util.Set;

/**
 * Implementation of PaginationService.
 */
public class EnrichedRecordPaginationService extends PaginationService<EnrichedRecord> {

    private EnrichedDataStore store;
    private String contextType;

    public EnrichedRecordPaginationService(EnrichedDataStore store, int pageSize, int maxGroupSize, String contextType) {
        super(pageSize, maxGroupSize);
        this.store = store;
        this.contextType = contextType;
    }

    @Override
    protected Map<String, Integer> getContextIdToNumOfItemsMap(String dataSource, TimeRange timeRange) {
        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(dataSource, timeRange.getStart(), timeRange.getEnd());
        return this.store.aggregateContextToNumOfEvents(enrichedRecordsMetadata, this.contextType);
    }


    @Override
    protected <U extends EnrichedRecord> PageIterator<U> getPageIterator(String dataSource, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems) {
        return new EnrichedRecordPageIterator<>(timeRange, this.contextType, dataSource, contextIds, this.store, this.getPageSize(), totalNumOfItems);
    }

    @Override
    protected void validateIndexes(String dataSource) {
        this.store.validateIndexes(dataSource, this.contextType);
    }

}
