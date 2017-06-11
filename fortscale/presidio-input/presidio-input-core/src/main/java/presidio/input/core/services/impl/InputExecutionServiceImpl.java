package presidio.input.core.services.impl;


import fortscale.common.general.Command;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.services.parameters.ParametersValidationService;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import presidio.input.core.services.api.InputExecutionService;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fortscale.common.general.CommonStrings.COMMAND_LINE_COMMMAND_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_DATA_SOURCE_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_DATE_FORMAT;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME;

public class InputExecutionServiceImpl implements InputExecutionService {

    private static final Logger logger = Logger.getLogger(InputExecutionServiceImpl.class);

    private final ParametersValidationService parameterValidationService;
    private final PresidioInputPersistencyService presidioInputPersistencyService;

    private DataSource dataSource;
    private long startDate;
    private long endDate;
    private Command command;

    public InputExecutionServiceImpl(ParametersValidationService parameterValidationService, PresidioInputPersistencyService presidioInputPersistencyService) {
        this.parameterValidationService = parameterValidationService;
        this.presidioInputPersistencyService = presidioInputPersistencyService;
    }

    private boolean init(String... params) throws Exception {
        logger.info("Setting and validating params:[{}] .", Arrays.toString(params));
        if (params.length < 3) {
            logger.error("Invalid input[{}]. Need at least {}, {} and {}. Example input: {}=some_{} {}=some_{}_as_long {}=some_{}_as_long.", params, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME);
            return false;
        }

        final String dataSourceParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_DATA_SOURCE_FIELD_NAME, params);
        final String startTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_START_DATE_FIELD_NAME, params);
        final String endTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_END_DATE_FIELD_NAME, params);
        final String commandParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_COMMMAND_FIELD_NAME, params);
        try {
            parameterValidationService.validateDatasourceParam(dataSourceParam);//todo:there should be only validation . there is parsing process in development.
        } catch (Exception e) {
            logger.error("Invalid input[{}].", params, e);
            return false;
        }
        command = Command.createCommand(commandParam);
        dataSource = DataSource.createDataSource(dataSourceParam);
        startDate = TimestampUtils.convertToSeconds(new SimpleDateFormat(COMMAND_LINE_DATE_FORMAT).parse(startTimeParam));
        endDate = TimestampUtils.convertToSeconds(new SimpleDateFormat(COMMAND_LINE_DATE_FORMAT).parse(endTimeParam));

        return true;
    }

    public void run(String... params) throws Exception {
        if (!init(params)) {
            logger.error("Bad command name {}. valid options are: {}", command, Command.values());
            throw new RuntimeException("Bad command name " + command);
        }
        switch (command) {
            case ENRICH:
                enrich();
                break;
            case CLEAN:
                clean();
                break;
        }
    }

    private void clean() {
        logger.info("Started clean processing with data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        presidioInputPersistencyService.clean(dataSource, startDate, endDate);
        logger.info("Finished enrich processing .");
    }

    private void enrich() throws Exception {
        logger.info("Started enrich processing with data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        final List<? extends AbstractAuditableDocument> dataRecords = find(dataSource, startDate, endDate);
        logger.info("Found {} dataRecords for dataSource:{}, startDate:{}, endDate:{}.", dataRecords, dataSource, startDate, endDate);

        final List<DlpFileEnrichedDocument> enrichedRecords = enrich(dataRecords);

        if (!storeForAde(enrichedRecords)) {
            logger.error("Failed to save!!!");
            //todo: how to handle?
        }

        logger.info("Finished enrich processing .");
    }

    private List<? extends AbstractAuditableDocument> find(DataSource dataSource, long startTime, long endTime) {
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

        //final boolean storeSuccessful = adeSdk.store(enrichedDocuments); //todo should be uncommented and replace temp implementation when adeSdk is ready
        /*temp*/
        System.out.println(enrichedDocuments);
        logger.info("*************input logic comes here***********");
        logger.info("enriched documents: \n{}", enrichedDocuments);
        logger.info("**********************************************");
        final boolean storeSuccessful = true;
        /*temp*/


        return storeSuccessful;
    }


}
