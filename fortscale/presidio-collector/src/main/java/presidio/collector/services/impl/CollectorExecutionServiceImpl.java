package presidio.collector.services.impl;

import fortscale.common.general.Command;
import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.services.parameters.ParametersValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.collector.services.api.CollectorExecutionService;
import presidio.collector.services.api.FetchService;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.services.CoreManagerService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fortscale.common.general.CommonStrings.*;

public class CollectorExecutionServiceImpl implements CollectorExecutionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private final CoreManagerService coreManagerService;
    private final FetchService fetchService;
    private final ParametersValidationService parameterValidationService;
    private final Command CLEAN_COMMAND = Command.CLEAN;

    public CollectorExecutionServiceImpl(CoreManagerService coreManagerService, FetchService fetchService, ParametersValidationService parameterValidationService) {
        this.coreManagerService = coreManagerService;
        this.fetchService = fetchService;
        this.parameterValidationService = parameterValidationService;
    }

    public void run(String... params) throws Exception {         //todo: we need to consider doing the fetch & store at the same iteration
        logger.info("Start collector processing with params: " + Arrays.toString(params));

        if (params.length < 4) {
            String errorMessage = String.format("Invalid input[%s]. Not enough parameters.", Arrays.toString(params));
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        final String dataSourceParam;
        final String startDateParam;
        final String endDateParam;
        final String commandParam;

        try {
            dataSourceParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_DATA_SOURCE_FIELD_NAME, params);
            startDateParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_START_DATE_FIELD_NAME, params);
            endDateParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_END_DATE_FIELD_NAME, params);
            commandParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_COMMAND_FIELD_NAME, params);
            parameterValidationService.validateDataSourceParam(dataSourceParam);
            parameterValidationService.validateCommand(commandParam);
            // TODO: set date format convention
            parameterValidationService.validateTimeParams(startDateParam, endDateParam);
        } catch (Exception e) {
            logger.error("Invalid input[{}].", params, e);
            return;
        }

        final DataSource dataSource = DataSource.createDataSource(dataSourceParam);
        final Instant startDate = Instant.parse(startDateParam);
        final Instant endDate = Instant.parse(endDateParam);
        final Command command = Command.createCommand(commandParam);


        if (command.equals(CLEAN_COMMAND)) {
            logger.info("Cleaning.");
            System.out.print("Cleaning.");
            return;
        }

        final List<String[]> fetchedDocuments;
        try {
            fetchedDocuments = fetch(dataSource, startDate, endDate);
        } catch (Exception e) {
            logger.error("HEY USER!!! FETCH FAILED! params: " + Arrays.toString(params), e);
            //todo: how do we handle? alert the user probably?
            return;
        }

        final List<AbstractAuditableDocument> createdDocuments = createDocuments(dataSource, fetchedDocuments);

        final boolean storeSuccessful = store(dataSource, createdDocuments);

        if (!storeSuccessful) {
            logger.error("store unsuccessful!!!");
            //todo: how do we handle?
        }

        logger.info("Finish csv processing");
    }

    private List<String[]> fetch(DataSource dataSource, Instant startTime, Instant endTime) throws Exception {
        logger.info("Start fetch");
        final List<String[]> fetchedRecords = fetchService.fetch(dataSource, startTime, endTime);//todo: maybe the retry logic will be here?
        logger.info("finish fetch");
        return fetchedRecords;
    }

    private boolean store(DataSource dataSource, List<AbstractAuditableDocument> fetchedDocuments) {
        logger.info("Start store");
        final boolean storeSuccessful = coreManagerService.store(dataSource, fetchedDocuments);
        logger.info("finish store");
        return storeSuccessful;
    }

    private List<AbstractAuditableDocument> createDocuments(DataSource dataSource, List<String[]> records) throws Exception {
        List<AbstractAuditableDocument> createdDocuments = new ArrayList<>();
        switch (dataSource) { //todo: we can use a document factory instead of switch case
            case DLPFILE: {
                for (String[] record : records) {
                    createdDocuments.add(new DlpFileDataDocument(record));
                }
                break;
            }
            case DLPMAIL: {
                throw new UnsupportedOperationException("DLPMAIL not supported yet");
            }
            case PRNLOG: {
                throw new UnsupportedOperationException("PRNLOG not supported yet");
            }
            default: {
                //should not happen
                throw new Exception("create documents failed. this is weird - should not happen. dataSource=" + dataSource.name()); //todo: temp
            }
        }

        return createdDocuments;

    }
}

