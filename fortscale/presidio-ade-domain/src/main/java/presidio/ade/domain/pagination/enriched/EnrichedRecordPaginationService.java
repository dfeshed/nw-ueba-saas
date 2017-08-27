package presidio.ade.domain.pagination.enriched;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.pagination.PaginationService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.util.List;
import java.util.Set;

/**
 * Implementation of PaginationService for enriched Records.
 */
public class EnrichedRecordPaginationService extends PaginationService<EnrichedRecord> {

    private static final Logger logger = Logger.getLogger(EnrichedRecordPaginationService.class);

    private EnrichedDataStore store;
    private String contextType;


    public EnrichedRecordPaginationService(EnrichedDataStore store, int pageSize, int maxGroupSize, String contextType) {
        super(pageSize, maxGroupSize);
        this.store = store;
        this.contextType = contextType;
    }

    public EnrichedRecordPaginationService(EnrichedDataStore store, int pageSize, int maxGroupSize, String contextType, String sortBy) {
        super(pageSize, maxGroupSize, sortBy);
        this.store = store;
        this.contextType = contextType;
    }

    @Override
    protected List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(String adeEventType, TimeRange timeRange) {
        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(adeEventType, timeRange.getStart(), timeRange.getEnd());
        return this.store.aggregateContextToNumOfEvents(enrichedRecordsMetadata, this.contextType);
    }

    @Override
    protected <U extends EnrichedRecord> PageIterator<U> createPageIterator(String adeEventType, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems) {
        int totalAmountOfPages = (int) Math.ceil((double) totalNumOfItems / this.getPageSize());
        logger.debug("Num of pages is: {}",totalAmountOfPages);
        return new EnrichedRecordPageIterator<>(timeRange, this.contextType, adeEventType, contextIds, this.store, this.getPageSize(), totalNumOfItems, totalAmountOfPages, this.getSortBy());
    }

    @Override
    protected void ensureContextAndDateTimeIndex(String adeEventType) {
        this.store.ensureContextAndDateTimeIndex(adeEventType, this.contextType);
    }

}
