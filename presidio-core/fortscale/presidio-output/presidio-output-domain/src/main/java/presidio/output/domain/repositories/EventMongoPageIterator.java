package presidio.output.domain.repositories;

import fortscale.common.general.Schema;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.services.event.EventPersistencyService;

import java.util.List;

public class EventMongoPageIterator<U extends EnrichedEvent> implements PageIterator<U> {

    private final EventPersistencyService eventPersistencyService;
    private int currentPage;
    private int totalAmountOfEvents;
    private int pageSize;
    private Schema schema;
    private String entityId;
    private String entityType;
    private TimeRange timeRange;
    private List<Pair<String, Object>> features;
    private int amountOfPages;

    public EventMongoPageIterator(EventPersistencyService eventPersistencyService, int pageSize, Schema schema, String entityId, TimeRange timeRange, List<Pair<String, Object>> features, int totalAmountOfEvents, String entityType) {
        this.eventPersistencyService = eventPersistencyService;
        this.currentPage = 0;
        this.totalAmountOfEvents = totalAmountOfEvents;
        this.pageSize = pageSize;
        this.schema = schema;
        this.entityId = entityId;
        this.entityType = entityType;
        this.timeRange = timeRange;
        this.features = features;
        amountOfPages = (int)Math.ceil(((double)totalAmountOfEvents)/ pageSize);
    }

    @Override
    public boolean hasNext() {
        return this.currentPage < amountOfPages;
    }

    @Override
    public List<U> next() {
        int numOfItemsToSkip = this.currentPage * this.pageSize;
        this.currentPage++;
        List<U> records = (List<U>) this.eventPersistencyService.readRecords(this.schema, this.entityId, this.timeRange, this.features, numOfItemsToSkip, this.pageSize, this.entityType);

        return records;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }
}
