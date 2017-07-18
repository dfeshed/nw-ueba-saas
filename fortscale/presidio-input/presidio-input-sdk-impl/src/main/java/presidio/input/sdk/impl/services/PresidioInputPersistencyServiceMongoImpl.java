package presidio.input.sdk.impl.services;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.DataService;
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
    public boolean store(DataSource dataSource, List<? extends AbstractAuditableDocument> records) {
        logger.info("Storing {} records for data source {}",
                records.size(), dataSource);

        return dataService.store(records, dataSource);
    }

    @Override
    public List<? extends AbstractAuditableDocument> find(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {
        logger.info("Finding records for data source:{}, from :{}, until :{}."
                , dataSource,
                startDate,
                endDate);
        return dataService.find(startDate, endDate, dataSource);
    }

    @Override
    public int clean(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {
        logger.info("Deleting records for data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataService.clean(startDate, endDate, dataSource);
    }

    @Override
    public void cleanAll(DataSource dataSource) throws Exception {
        dataService.cleanAll(dataSource);
    }
}
