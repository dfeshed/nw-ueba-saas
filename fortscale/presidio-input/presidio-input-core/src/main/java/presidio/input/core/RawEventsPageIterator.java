package presidio.input.core;

import fortscale.common.general.Schema;
import fortscale.utils.pagination.PageIterator;
import presidio.sdk.api.domain.AbstractPresidioDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.List;

public class RawEventsPageIterator<U extends AbstractPresidioDocument> implements PageIterator<U> {

    private final Instant startDate;
    private final Instant endDate;
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final Schema schema;
    private int currentPage;
    private int totalAmountOfPages;
    private int pageSize;

    /**
     * @param presidioInputPersistencyService
     * @param schema                          ade evnet type
     * @param pageSize                        num of events in each page
     */
    public RawEventsPageIterator(Instant startDate, Instant endDate, PresidioInputPersistencyService presidioInputPersistencyService, Schema schema, int pageSize) {
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.currentPage = 0;
        this.schema = schema;
        this.pageSize = pageSize;
        this.startDate = startDate;
        this.endDate = endDate;
        long totalNumberOfEvents = presidioInputPersistencyService.count(schema, startDate, endDate);
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

        return this.presidioInputPersistencyService.readRecords(this.schema, this.startDate, this.endDate, numOfItemsToSkip, this.pageSize);
    }

}
