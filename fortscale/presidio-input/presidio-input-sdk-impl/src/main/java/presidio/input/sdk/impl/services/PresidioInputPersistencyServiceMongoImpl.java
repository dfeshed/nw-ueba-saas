package presidio.input.sdk.impl.services;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.PresidioSchemas;
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
    public boolean store(PresidioSchemas presidioSchemas, List<? extends AbstractAuditableDocument> records) {
        logger.info("Storing {} records for data source {}",
                records.size(), presidioSchemas);

        return dataService.store(records, presidioSchemas);
    }

    @Override
    public List<? extends AbstractAuditableDocument> find(PresidioSchemas presidioSchemas, Instant startDate, Instant endDate) throws Exception {
        logger.info("Finding records for data source:{}, from :{}, until :{}."
                , presidioSchemas,
                startDate,
                endDate);
        return dataService.find(startDate, endDate, presidioSchemas);
    }

    @Override
    public int clean(PresidioSchemas presidioSchemas, Instant startDate, Instant endDate) throws Exception {
        logger.info("Deleting records for data source:{}, from {}:{}, until {}:{}."
                , presidioSchemas,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataService.clean(startDate, endDate, presidioSchemas);
    }

    @Override
    public void cleanAll(PresidioSchemas presidioSchemas) throws Exception {
        dataService.cleanAll(presidioSchemas);
    }
}
