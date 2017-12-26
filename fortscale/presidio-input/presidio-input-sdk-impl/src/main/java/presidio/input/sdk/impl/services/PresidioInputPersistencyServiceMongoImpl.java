package presidio.input.sdk.impl.services;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.services.DataService;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.List;

public class PresidioInputPersistencyServiceMongoImpl implements PresidioInputPersistencyService {
    private final Logger logger = Logger.getLogger(PresidioInputPersistencyServiceMongoImpl.class);

    private final DataService dataService;

    public PresidioInputPersistencyServiceMongoImpl(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public boolean store(Schema schema, List<? extends AbstractAuditableDocument> records) {
        logger.trace("Storing {} records for data source {}",
                records.size(), schema);

        return dataService.store(records, schema);
    }

    @Override
    public List<? extends AbstractAuditableDocument> find(Schema schema, Instant startDate, Instant endDate) throws Exception {
        logger.info("Finding records for data source:{}, from :{}, until :{}."
                , schema,
                startDate,
                endDate);
        return dataService.find(startDate, endDate, schema);
    }

    @Override
    public int clean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        logger.info("Deleting records for data source:{}, from {}:{}, until {}:{}."
                , schema,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataService.clean(startDate, endDate, schema);
    }

    @Override
    public int cleanUntil(Schema schema, Instant endDate) throws Exception {
        logger.info("Deleting records for data source:{}, until {}:{}."
                , schema,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataService.cleanUntil(endDate, schema);
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        dataService.cleanAll(schema);
    }

    @Override
    public <U extends AbstractInputDocument> List<U> readRecords(Schema schema, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize) {
        return dataService.readRecords(schema, startDate, endDate, numOfItemsToSkip, pageSize);
    }

    @Override
    public long count(Schema schema, Instant startDate, Instant endDate) {
        return dataService.count(schema, startDate, endDate);
    }
}
