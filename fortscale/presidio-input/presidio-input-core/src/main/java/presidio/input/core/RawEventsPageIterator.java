package presidio.input.core;

import fortscale.common.general.Schema;
import fortscale.utils.pagination.PageIterator;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RawEventsPageIterator<U extends AbstractInputDocument> implements PageIterator<U> {

    private final Instant startDate;
    private final Instant endDate;
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final Schema schema;
    private int currentPage;
    private int totalAmountOfPages;
    private int pageSize;
    private Map<String, Object> filter;
    private  List<String> projectionFields;
    private Class clazz;
    /**
     *
     * @param startDate startDate
     * @param endDate endDate
     * @param presidioInputPersistencyService presidioInputPersistencyService
     * @param schema schema
     * @param pageSize um of events in each page
     */
    public RawEventsPageIterator(Instant startDate, Instant endDate, PresidioInputPersistencyService presidioInputPersistencyService, Schema schema, int pageSize) {
        this(startDate, endDate, presidioInputPersistencyService, schema, pageSize, Collections.emptyMap(),  Collections.emptyList(), null);
    }

    /**
     *
     * @param startDate startDate
     * @param endDate endDate
     * @param presidioInputPersistencyService presidioInputPersistencyService
     * @param schema schema
     * @param pageSize um of events in each page
     * @param filter filter
     */
    public RawEventsPageIterator(Instant startDate, Instant endDate, PresidioInputPersistencyService presidioInputPersistencyService,
                                 Schema schema, int pageSize, Map<String, Object> filter,  List<String> projectionFields, Class clazz) {
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.currentPage = 0;
        this.schema = schema;
        this.pageSize = pageSize;
        this.startDate = startDate;
        this.endDate = endDate;
        this.filter = filter;
        this.projectionFields = projectionFields;
        this.clazz = clazz;
        float totalNumberOfEvents;
        totalNumberOfEvents = presidioInputPersistencyService.count(schema, startDate, endDate, filter, projectionFields);
        this.totalAmountOfPages = (int) Math.ceil(totalNumberOfEvents / pageSize);
    }



    @Override
    public boolean hasNext() {
        return this.currentPage < this.totalAmountOfPages;
    }

    /**
     * Call to the store with meta date(ade event type etc...), list of context ids, num of items to skip and num of items to read.
     *
     * @return list of <U>
     */
    @Override
    public List<U> next() {
        int numOfItemsToSkip = this.currentPage * this.pageSize;
        this.currentPage++;
        List<U> records;

        if(!(filter.isEmpty() && projectionFields.isEmpty()) && clazz != null ) {
            records = this.presidioInputPersistencyService.readRecords(this.schema, this.startDate, this.endDate, numOfItemsToSkip, this.pageSize, filter, projectionFields, clazz);
        }
        else{
            records = this.presidioInputPersistencyService.readRecords(this.schema, this.startDate, this.endDate, numOfItemsToSkip, this.pageSize);
        }

        return records;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }
}
