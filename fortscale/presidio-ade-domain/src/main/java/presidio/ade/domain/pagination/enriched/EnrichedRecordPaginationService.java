package presidio.ade.domain.pagination.enriched;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.pagination.PaginationService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.impl.EnrichedRecordPageIterator;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.util.List;
import java.util.Set;

/**
 * Implementation of PaginationService for enriched Records.
 */
public class EnrichedRecordPaginationService extends PaginationService<EnrichedRecord> {

    private EnrichedDataStore store;
    private String contextType;
    private static final Logger logger = Logger.getLogger(EnrichedRecordPaginationService.class);

    public EnrichedRecordPaginationService(EnrichedDataStore store, int pageSize, int maxGroupSize, String contextType) {
        super(pageSize, maxGroupSize);
        this.store = store;
        this.contextType = contextType;
    }

    @Override
    protected List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(String dataSource, TimeRange timeRange) {
        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(dataSource, timeRange.getStart(), timeRange.getEnd());
        return this.store.aggregateContextToNumOfEvents(enrichedRecordsMetadata, this.contextType);
    }

    @Override
    protected <U extends EnrichedRecord> PageIterator<U> createPageIterator(String dataSource, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems) {
        int totalAmountOfPages = (int) Math.ceil((double) totalNumOfItems / this.getPageSize());
        logger.debug("Num of pages is: {}",totalAmountOfPages);
        return new EnrichedRecordPageIterator<>(timeRange, this.contextType, dataSource, contextIds, this.store, this.getPageSize(), totalNumOfItems, totalAmountOfPages);
    }

    @Override
    protected void ensureContextAndDateTimeIndex(String dataSource) {
        this.store.ensureContextAndDateTimeIndex(dataSource, this.contextType);
    }

}
