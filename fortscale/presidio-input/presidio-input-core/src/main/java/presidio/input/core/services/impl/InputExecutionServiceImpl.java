package presidio.input.core.services.impl;


import fortscale.common.general.Datasource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.services.parameters.ParametersValidationService;
import fortscale.utils.logging.Logger;
import presidio.input.core.services.api.InputExecutionService;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;
import presidio.sdk.api.services.PresidioInputSdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fortscale.common.general.CommonStrings.COMMAND_LINE_DATASOURCE_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_END_TIME_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_START_TIME_FIELD_NAME;

public class InputExecutionServiceImpl implements InputExecutionService {

    private static final Logger logger = Logger.getLogger(InputExecutionServiceImpl.class);

    private final ParametersValidationService parameterValidationService;
    private final PresidioInputSdk inputSdk;

    public InputExecutionServiceImpl(ParametersValidationService parameterValidationService, PresidioInputSdk inputSdk) {
        this.parameterValidationService = parameterValidationService;
        this.inputSdk = inputSdk;
    }

    public void run(String... params) throws Exception {
        logger.info("Started collector processing with params: ." + Arrays.toString(params));

        if (params.length < 3) {
            logger.error("Invalid input[{}]. Need at least {}, {} and {}. Example input: {}=some_{} {}=some_{}_as_long {}=some_{}_as_long.", params, COMMAND_LINE_DATASOURCE_FIELD_NAME, COMMAND_LINE_START_TIME_FIELD_NAME, COMMAND_LINE_END_TIME_FIELD_NAME, COMMAND_LINE_DATASOURCE_FIELD_NAME, COMMAND_LINE_DATASOURCE_FIELD_NAME, COMMAND_LINE_START_TIME_FIELD_NAME, COMMAND_LINE_START_TIME_FIELD_NAME, COMMAND_LINE_END_TIME_FIELD_NAME, COMMAND_LINE_END_TIME_FIELD_NAME);
            return;
        }

        final String dataSourceParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_DATASOURCE_FIELD_NAME, params);
        final String startTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_START_TIME_FIELD_NAME, params);
        final String endTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_END_TIME_FIELD_NAME, params);
        try {
            parameterValidationService.validateDatasourceParam(dataSourceParam);
            parameterValidationService.validateTimeParams(startTimeParam, endTimeParam);
        } catch (Exception e) {
            logger.error("Invalid input[{}].", params, e);
            return;
        }

        final Datasource datasource;
        final long startTime;
        final long endTime;
        datasource = Datasource.createDataSource(dataSourceParam);
        startTime = Long.parseLong(startTimeParam);
        endTime = Long.parseLong(endTimeParam);

        final List<? extends AbstractAuditableDocument> dataRecords = find(datasource, startTime, endTime);
        logger.info("Found {} dataRecords for datasource:{}, startTime:{}, endTime:{}.", datasource, startTime, endTime);

        final List<DlpFileEnrichedDocument> enrichedRecords = enrich(dataRecords);

        if (storeForAde(enrichedRecords)) {
            logger.error("Failed to save!!!");
            //todo: how to handle?
        }

        logger.info("Finished collector processing with params: ." + Arrays.toString(params));
    }

    private List<? extends AbstractAuditableDocument> find(Datasource datasource, long startTime, long endTime) {
        logger.debug("Finding {} records for datasource:{}, startTime:{}, endTime:{}.", datasource, startTime, endTime);
        return inputSdk.find(datasource, startTime, endTime);
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

    private boolean storeForAde(List<? extends AbstractAuditableDocument> fetchedDocuments) {
        logger.debug("Storing {} records.", fetchedDocuments.size());

        //final boolean storeSuccessful = adeSdk.store(fetchedDocuments); //todo should be uncommented and replace temp implementation when adeSdk is ready
        /*temp*/
        System.out.println(fetchedDocuments);
        final boolean storeSuccessful = true;
        /*temp*/


        return storeSuccessful;
    }


}
