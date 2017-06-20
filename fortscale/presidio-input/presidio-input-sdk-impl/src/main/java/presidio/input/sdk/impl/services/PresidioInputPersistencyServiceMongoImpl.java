package presidio.input.sdk.impl.services;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.DataService;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class PresidioInputPersistencyServiceMongoImpl implements PresidioInputPersistencyService {
    private final Logger logger = Logger.getLogger(PresidioInputPersistencyServiceMongoImpl.class);

    private final DataService dataService;

    public PresidioInputPersistencyServiceMongoImpl(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public boolean store(DataSource dataSource, List<AbstractAuditableDocument> records) {
        //TODO: change this when we have the new service and repo
        logger.info("Storing {} records for data source {}",
                records.size(), dataSource);

        List<DlpFileDataDocument> dlpFileDataDocuments = records // todo: this is very ad-hoc. we need to design a mechanism for resolving the right repo and casting
                .stream()
                .map(e -> (DlpFileDataDocument) e)
                .collect(Collectors.toList());
        return dataService.store(dlpFileDataDocuments);
    }

    @Override
    public List<? extends AbstractAuditableDocument> find(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {
        logger.info("Finding records for data source:{}, from :{}, until :{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataServiceForDataSource(dataSource).find(startDate, endDate);
    }

    @Override
    public int clean(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {
        logger.info("Deleting records for data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataServiceForDataSource(dataSource).clean(startDate, endDate);
    }

    @Override
    public void cleanAll(DataSource dataSource) throws Exception {
        dataServiceForDataSource(dataSource).cleanAll();
    }


    private DataService dataServiceForDataSource(DataSource dataSource) throws Exception {
        switch (dataSource) {
            case DLPFILE:
                return dataService;
            default:
                String errorMessage = String.format("Can't find data service for data source %s.", dataSource);
                logger.error(errorMessage);
                throw new Exception(errorMessage);
        }
    }
}
