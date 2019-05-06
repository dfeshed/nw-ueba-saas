package presidio.output.domain.repositories;

import fortscale.common.general.Schema;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.output.domain.records.events.EnrichedUserEvent;
import presidio.output.domain.services.event.EventPersistencyService;

import java.util.List;

public class EventMongoPageIterator<U extends EnrichedUserEvent> implements PageIterator<U> {

    private final EventPersistencyService eventPersistencyService;
    private int currentPage;
    private int totalAmountOfEvents;
    private int pageSize;
    private Schema schema;
    private String userId;
    private TimeRange timeRange;
    private List<Pair<String, Object>> features;
    private int amountOfPages;

    public EventMongoPageIterator(EventPersistencyService eventPersistencyService, int pageSize, Schema schema, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int totalAmountOfEvents) {
        this.eventPersistencyService = eventPersistencyService;
        this.currentPage = 0;
        this.totalAmountOfEvents = totalAmountOfEvents;
        this.pageSize = pageSize;
        this.schema = schema;
        this.userId = userId;
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
        List<U> records = (List<U>) this.eventPersistencyService.findUserEvents(this.schema, this.userId, this.timeRange, this.features, numOfItemsToSkip, this.pageSize);

        return records;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }
}
