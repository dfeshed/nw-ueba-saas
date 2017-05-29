package presidio.collector.services.impl;

import fortscale.common.general.Datasource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.services.parameters.ParametersValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.collector.services.api.CollectorExecutionService;
import presidio.collector.services.api.FetchService;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.services.CoreManagerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fortscale.common.general.CommonStrings.COMMAND_LINE_DATASOURCE_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_END_TIME_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_START_TIME_FIELD_NAME;

public class CollectorExecutionServiceImpl implements CollectorExecutionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private final CoreManagerService coreManagerService;
    private final FetchService fetchService;
    private final ParametersValidationService parameterValidationService;

    public CollectorExecutionServiceImpl(CoreManagerService coreManagerService, FetchService fetchService, ParametersValidationService parameterValidationService) {
        this.coreManagerService = coreManagerService;
        this.fetchService = fetchService;
        this.parameterValidationService = parameterValidationService;
    }

    public void run(String... params) throws Exception {         //todo: we need to consider doing the fetch & store at the same iteration
        logger.info("Start collector processing with params: " + Arrays.toString(params));

        if (params.length < 3) {
            logger.error("Invalid input[{}]. Need at least {}, {} and {}. Example input: {}=some_{} {}=some_{}_as_long {}=some_{}_as_long", params, COMMAND_LINE_DATASOURCE_FIELD_NAME, COMMAND_LINE_START_TIME_FIELD_NAME, COMMAND_LINE_END_TIME_FIELD_NAME, COMMAND_LINE_DATASOURCE_FIELD_NAME, COMMAND_LINE_DATASOURCE_FIELD_NAME, COMMAND_LINE_START_TIME_FIELD_NAME, COMMAND_LINE_START_TIME_FIELD_NAME, COMMAND_LINE_END_TIME_FIELD_NAME, COMMAND_LINE_END_TIME_FIELD_NAME);
            return;
        }

        final String dataSourceParam;
        final String startTimeParam;
        final String endTimeParam;

        try {
            dataSourceParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_DATASOURCE_FIELD_NAME, params);
            startTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_START_TIME_FIELD_NAME, params);
            endTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_END_TIME_FIELD_NAME, params);
            parameterValidationService.validateDatasourceParam(dataSourceParam);
            parameterValidationService.validateTimeParams(startTimeParam, endTimeParam);
        } catch (Exception e) {
            logger.error("Invalid input[{}].", params, e);
            return;
        }

        final Datasource dataSource;
        final long startTime;
        final long endTime;
        dataSource = Datasource.createDataSource(dataSourceParam);
        startTime = Long.parseLong(startTimeParam);
        endTime = Long.parseLong(endTimeParam);


        final List<String[]> fetchedDocuments;
        try {
            fetchedDocuments = fetch(dataSource, startTime, endTime);
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

    private List<String[]> fetch(Datasource dataSource, long startTime, long endTime) throws Exception {
        logger.info("Start fetch");
        final List<String[]> fetchedRecords = fetchService.fetch(dataSource, startTime, endTime);//todo: maybe the retry logic will be here?
        logger.info("finish fetch");
        return fetchedRecords;
    }

    private boolean store(Datasource dataSource, List<AbstractAuditableDocument> fetchedDocuments) {
        logger.info("Start store");
        final boolean storeSuccessful = coreManagerService.store(dataSource, fetchedDocuments);
        logger.info("finish store");
        return storeSuccessful;
    }

    private List<AbstractAuditableDocument> createDocuments(Datasource datasource, List<String[]> records) throws Exception {
        List<AbstractAuditableDocument> createdDocuments = new ArrayList<>();
        switch (datasource) { //todo: we can use a document factory instead of switch case
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
                throw new Exception("create documents failed. this is weird - should not happen. datasource=" + datasource.name()); //todo: temp
            }
        }

        return createdDocuments;

    }


}

