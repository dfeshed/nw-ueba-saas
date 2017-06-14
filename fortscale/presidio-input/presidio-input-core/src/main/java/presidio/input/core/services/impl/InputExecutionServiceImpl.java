package presidio.input.core.services.impl;


import fortscale.common.general.Command;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.services.parameters.ParametersValidationService;
import fortscale.utils.logging.Logger;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.input.core.services.api.InputExecutionService;
import presidio.input.core.services.converters.DlpFileConverter;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fortscale.common.general.CommonStrings.*;

public class InputExecutionServiceImpl implements InputExecutionService {

    private static final Logger logger = Logger.getLogger(InputExecutionServiceImpl.class);
    private static final String[] MANDATORY_PARAMS = {
            COMMAND_LINE_DATA_SOURCE_FIELD_NAME,
            COMMAND_LINE_START_DATE_FIELD_NAME,
            COMMAND_LINE_END_DATE_FIELD_NAME,
            COMMAND_LINE_COMMAND_FIELD_NAME
    };

    private final ParametersValidationService parameterValidationService;
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final EnrichedDataStore enrichedDataStore;

    private DataSource dataSource; //todo maor
    private Instant startDate;
    private Instant endDate;
    private Command command;

    public InputExecutionServiceImpl(ParametersValidationService parameterValidationService,
                                     PresidioInputPersistencyService presidioInputPersistencyService,
                                     EnrichedDataStore enrichedDataStore) {
        this.parameterValidationService = parameterValidationService;
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.enrichedDataStore = enrichedDataStore;
    }

    private void init(String... params) throws Exception {
        logger.info("Setting and validating params:[{}] .", Arrays.toString(params));
        if (params.length < MANDATORY_PARAMS.length) {
            String errorMessage = String.format("Invalid input[%s]. Not enough parameters, Need at least %s.", Arrays.toString(params), Arrays.toString(MANDATORY_PARAMS));
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        final String dataSourceParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_DATA_SOURCE_FIELD_NAME, params);
        final String startDateParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_START_DATE_FIELD_NAME, params);
        final String endDateParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_END_DATE_FIELD_NAME, params);
        final String commandParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_COMMAND_FIELD_NAME, params);
        try {
            parameterValidationService.validateDataSourceParam(dataSourceParam);//todo:there should be only validation . there is parsing process in development.
            parameterValidationService.validateTimeParams(startDateParam, endDateParam);
            parameterValidationService.validateCommand(commandParam);
        } catch (Exception e) {
            String errorMessage = String.format("Invalid input[%s}].", Arrays.toString(params));
            logger.error(errorMessage, e);
            String userMessage = errorMessage + " " + e.getMessage();
            throw new Exception(userMessage, e);
        }
        command = Command.createCommand(commandParam);
        dataSource = DataSource.createDataSource(dataSourceParam);
        startDate = Instant.parse(startDateParam);
        endDate = Instant.parse(endDateParam);
    }

    public void run(String... params) throws Exception {
        init(params);
        switch (command) {
            case ENRICH:
                enrich();
                break;
            case CLEAN:
                clean();
                break;
            //todo: add support for cleanAll
            default:
                throw new UnsupportedOperationException("Unsupported command " + command);
        }
    }

    private void cleanAll() throws Exception {
        logger.info("Started clean processing for data source:{}.", dataSource);
        presidioInputPersistencyService.cleanAll(dataSource);
    }

    private void clean() throws Exception {
        logger.info("Started clean processing for data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        presidioInputPersistencyService.clean(dataSource, startDate.getEpochSecond(), endDate.getEpochSecond());
        logger.info("Finished enrich processing .");
    }

    private void enrich() throws Exception {
        logger.info("Started enrich processing with data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        final List<? extends AbstractAuditableDocument> dataRecords = find(dataSource, startDate.getEpochSecond(), endDate.getEpochSecond());
        logger.info("Found {} dataRecords for dataSource:{}, startDate:{}, endDate:{}.", dataRecords, dataSource, startDate, endDate);

        final List<DlpFileEnrichedDocument> enrichedRecords = enrich(dataRecords);

        if (!storeForAde(enrichedRecords)) {
            logger.error("Failed to save!!!");
            //todo: how to handle?
        }

        logger.info("Finished enrich processing .");
    }

    private List<? extends AbstractAuditableDocument> find(DataSource dataSource, long startTime, long endTime) throws Exception {
        logger.debug("Finding records for data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return presidioInputPersistencyService.find(dataSource, startTime, endTime);
    }

    private List<DlpFileEnrichedDocument> enrich(List<? extends AbstractAuditableDocument> dataRecords) { //THIS IS A TEMP IMPLEMENTATION!!!!!!!!!!
        //todo: again, very ad-hoc. maybe we should create an enrichment service
        List<DlpFileEnrichedDocument> enrichedRecords = new ArrayList<>();
        for (AbstractAuditableDocument dataRecord : dataRecords) {
            final DlpFileDataDocument dlpfileDataRecord = (DlpFileDataDocument) dataRecord;
            enrichedRecords.add(new DlpFileEnrichedDocument(dlpfileDataRecord, dlpfileDataRecord.getUsername(), dlpfileDataRecord.getHostname()));
        }


        return enrichedRecords;
    }

    private boolean storeForAde(List<? extends AbstractAuditableDocument> enrichedDocuments) {
        logger.debug("Storing {} records.", enrichedDocuments.size());


        EnrichedRecordsMetadata recordsMetaData = new EnrichedRecordsMetadata(dataSource.toString(), startDate, endDate);
        List<? extends EnrichedRecord> records = convert(enrichedDocuments, new DlpFileConverter());

        enrichedDataStore.store(recordsMetaData, records);

        logger.info("*************input logic comes here***********");
        logger.info("enriched documents: \n{}", enrichedDocuments);
        logger.info("**********************************************");
        final boolean storeSuccessful = true;
        /*temp*/
        return storeSuccessful;
    }

    protected List<EnrichedRecord> convert(List<? extends AbstractAuditableDocument> enrichedDocuments,
                                           DlpFileConverter converter) {
        List<EnrichedRecord> records = new ArrayList<>();
        enrichedDocuments.forEach(doc -> records.add(converter.convert((DlpFileEnrichedDocument) doc)));
        return records;
    }
}
