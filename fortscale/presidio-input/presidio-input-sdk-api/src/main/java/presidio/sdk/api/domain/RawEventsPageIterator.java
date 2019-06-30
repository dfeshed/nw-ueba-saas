package presidio.sdk.api.domain;

import fortscale.common.general.Schema;
import fortscale.utils.pagination.PageIterator;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.List;

public class RawEventsPageIterator<T extends AbstractInputDocument> implements PageIterator<T> {
    private final Instant startDate;
    private final Instant endDate;
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final Schema schema;
    private final int pageSize;

    private int totalNumberOfPages;
    private int currentPage;

    /**
     * Constructor.
     *
     * @param schema   Event type (e.g. {@link Schema#AUTHENTICATION}).
     * @param pageSize Number of events in each page.
     */
    public RawEventsPageIterator(
            Instant startDate,
            Instant endDate,
            PresidioInputPersistencyService presidioInputPersistencyService,
            Schema schema,
            int pageSize) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.schema = schema;
        this.pageSize = pageSize;

        float totalNumberOfEvents = presidioInputPersistencyService.count(schema, startDate, endDate);
        totalNumberOfPages = (int)Math.ceil(totalNumberOfEvents / pageSize);
        currentPage = 0;
    }

    @Override
    public boolean hasNext() {
        return currentPage < totalNumberOfPages;
    }

    /**
     * Call the store with the metadata (schema, etc.), number of items to skip and number of items to read.
     *
     * @return A list of <T>s.
     */
    @Override
    public List<T> next() {
        int numOfItemsToSkip = currentPage * pageSize;
        currentPage++;
        return presidioInputPersistencyService.readRecords(schema, startDate, endDate, numOfItemsToSkip, pageSize);
    }
}
