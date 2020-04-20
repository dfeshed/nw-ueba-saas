package presidio.sdk.api.domain;

import fortscale.common.general.Schema;
import fortscale.utils.pagination.PageIterator;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class RawEventsPageIterator<T extends AbstractInputDocument> implements PageIterator<T> {
    private final Instant startDate;
    private final Instant endDate;
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final Schema schema;
    private final int pageSize;
    private final Map<String, Object> filters;
    private final List<String> projectionFields;

    private int totalNumberOfPages;
    private int currentPage;


    /**
     * Constructor.
     *
     * @param startDate                       Start date.
     * @param endDate                         End date.
     * @param presidioInputPersistencyService Presidio input persistency service.
     * @param schema                          Event type (e.g. {@link Schema#AUTHENTICATION}).
     * @param pageSize                        Number of events in each page.
     * @param filters                         Filters.
     * @param projectionFields                Projection fields.
     */
    public RawEventsPageIterator(
            Instant startDate,
            Instant endDate,
            PresidioInputPersistencyService presidioInputPersistencyService,
            Schema schema,
            int pageSize,
            Map<String, Object> filters,
            List<String> projectionFields) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.schema = schema;
        this.pageSize = pageSize;
        this.filters = filters;
        this.projectionFields = projectionFields;

        float totalNumberOfEvents = presidioInputPersistencyService.count(
                schema, startDate, endDate, filters, projectionFields);
        totalNumberOfPages = (int)Math.ceil(totalNumberOfEvents / pageSize);
        currentPage = 0;
    }

    /**
     * Constructor.
     *
     * @see #RawEventsPageIterator(Instant, Instant, PresidioInputPersistencyService, Schema, int, Map, List).
     */
    public RawEventsPageIterator(
            Instant startDate,
            Instant endDate,
            PresidioInputPersistencyService presidioInputPersistencyService,
            Schema schema,
            int pageSize) {

        this(startDate, endDate, presidioInputPersistencyService, schema, pageSize, emptyMap(), emptyList());
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
        int numberOfItemsToSkip = currentPage * pageSize;
        currentPage++;
        return !filters.isEmpty() || !projectionFields.isEmpty() ?
                presidioInputPersistencyService.readRecords(
                        schema, startDate, endDate, numberOfItemsToSkip, pageSize, filters, projectionFields) :
                presidioInputPersistencyService.readRecords(
                        schema, startDate, endDate, numberOfItemsToSkip, pageSize);
    }
}
