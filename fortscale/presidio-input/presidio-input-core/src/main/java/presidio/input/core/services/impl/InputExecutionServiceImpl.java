package presidio.input.core.services.impl;


import fortscale.common.general.Datasource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.services.parameters.ParametersValidationService;
import fortscale.utils.logging.Logger;
import fortscale.utils.shell.service.PresidioExecutionService;
import fortscale.utils.time.TimestampUtils;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InputExecutionServiceImpl implements PresidioExecutionService {

    private static final Logger logger = Logger.getLogger(InputExecutionServiceImpl.class);

    private final ParametersValidationService parameterValidationService;
    private final PresidioInputPersistencyService presidioInputPersistencyService;

    public InputExecutionServiceImpl(ParametersValidationService parameterValidationService, PresidioInputPersistencyService presidioInputPersistencyService) {
        this.parameterValidationService = parameterValidationService;
        this.presidioInputPersistencyService = presidioInputPersistencyService;
    }

    @Override
    public void process(String dataSource, String startTime, String endTime) throws Exception {

        logger.info("Started input processing for data source {} between start date {} to end date {}" + dataSource, startTime, endTime);

//        if (params.length < 3) {
//            logger.error("Invalid input[{}]. Need at least {}, {} and {}. Example input: {}=some_{} {}=some_{}_as_long {}=some_{}_as_long.", params, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME);
//            return;
//        }

        //TODO remove validator un-used methods from the validator class
//        final String dataSourceParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_DATA_SOURCE_FIELD_NAME, params);
//        final String startTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_START_DATE_FIELD_NAME, params);
//        final String endTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_END_DATE_FIELD_NAME, params);
        //TODO can we validate data source enum with spring cli?
        try {
            parameterValidationService.validateDatasourceParam(dataSource);
//            parameterValidationService.validateTimeParams(startTimeParam, endTimeParam);
        } catch (Exception e) {
            //TODO this will be part of the parsing- can be removed from here
//            logger.error("Invalid input[{}].", params, e);
            return;
        }

        //TODO change parameter type to Enum
        final Datasource datasource = Datasource.createDataSource(dataSource);
        //TODO- this can be done as part of the command parser
        final long startTimeLong = TimestampUtils.convertToSeconds(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(startTime));
        final long endTimeLong = TimestampUtils.convertToSeconds(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(endTime));

        final List<? extends AbstractAuditableDocument> dataRecords = find(datasource, startTimeLong, endTimeLong);
        logger.info("Found {} dataRecords for datasource:{}, startTime:{}, endTime:{}.", datasource, startTimeLong, endTimeLong);

        final List<DlpFileEnrichedDocument> enrichedRecords = enrich(dataRecords);

        if (!storeForAde(enrichedRecords)) {
            logger.error("Failed to save!!!");
            //todo: how to handle?
        }

        logger.info("Finished input processing for data source {} between start date {} to end date {}" + dataSource, startTime, endTime);
    }

    private List<? extends AbstractAuditableDocument> find(Datasource datasource, long startTime, long endTime) {
        logger.debug("Finding {} records for datasource:{}, startTime:{}, endTime:{}.", datasource, startTime, endTime);
        return presidioInputPersistencyService.find(datasource, startTime, endTime);
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

    @Override
    public void clean(String dataSource, String startTime, String endTime) {

    }
}
