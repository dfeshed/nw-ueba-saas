package presidio.collector.services.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.collector.Datasource;
import presidio.collector.services.api.CollectorExecutionService;
import presidio.collector.services.api.FetchService;
import presidio.sdk.api.domain.AbstractRecordDocument;
import presidio.sdk.api.domain.DlpFileRecordDocumentBuilder;
import presidio.sdk.api.services.CoreManagerSdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectorExecutionServiceImpl implements CollectorExecutionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DATASOURCE_FIELD_NAME = "datasource";
    private static final String START_TIME_FIELD_NAME = "start_time";
    private static final String END_TIME_FIELD_NAME = "end_time";
    private static final String PARAM_DELIMITER = "=";

    private final CoreManagerSdk coreManagerSdk;
    private final FetchService fetchService;

    public CollectorExecutionServiceImpl(CoreManagerSdk coreManagerSdk, FetchService fetchService) {
        this.coreManagerSdk = coreManagerSdk;
        this.fetchService = fetchService;
    }

    public void run(String... params) throws Exception {         //todo: we need to consider doing the fetch & store at the same iteration
        logger.info("Start collector processing with params: " + Arrays.toString(params));

        if (params.length < 3) {
            final String errorMessage = String.format("Need at least %s, %s and %s. Example input: %s=some_%s %s=some_%s_as_long %s=some_%s_as_long", DATASOURCE_FIELD_NAME, START_TIME_FIELD_NAME, END_TIME_FIELD_NAME, DATASOURCE_FIELD_NAME, DATASOURCE_FIELD_NAME, START_TIME_FIELD_NAME, START_TIME_FIELD_NAME, END_TIME_FIELD_NAME, END_TIME_FIELD_NAME);
            logger.error("Invalid input[{}]. {}", params, errorMessage);
            return;
        }

        final Datasource dataSource;
        final long startTime;
        final long endTime;
        try {
            dataSource = Datasource.createDataSource(getMandatoryParamAsString(DATASOURCE_FIELD_NAME, params));
            startTime = Long.parseLong(getMandatoryParamAsString(START_TIME_FIELD_NAME, params));
            endTime = Long.parseLong(getMandatoryParamAsString(END_TIME_FIELD_NAME, params));
        } catch (Exception e) {
            logger.error("Invalid input[{}].", params, e);
            return;
        }

        try {
            validateParams(startTime, endTime); // datasource is already validated during its creation
        } catch (Exception e) {
            logger.error("Invalid input[{}].", params, e);
            return;
        }

        final List<String[]> fetchedDocuments;
        try {
            fetchedDocuments = fetch(dataSource, startTime, endTime);
        } catch (Exception e) {
            logger.error("HEY USER!!! FETCH FAILED! params: " + Arrays.toString(params), e);
            //todo: how do we handle? alert the user probably?
            return;
        }

        final List<AbstractRecordDocument> createdDocuments = createDocuments(dataSource, fetchedDocuments);

        final boolean storeSuccessful = store(createdDocuments);

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

    private boolean store(List<AbstractRecordDocument> fetchedDocuments) {
        logger.info("Start store");
        final boolean storeSuccessful = coreManagerSdk.store(fetchedDocuments);
        logger.info("finish store");
        return storeSuccessful;
    }

    private List<AbstractRecordDocument> createDocuments(Datasource datasource, List<String[]> records) throws Exception {
        List<AbstractRecordDocument> createdDocuments = new ArrayList<>();
        switch (datasource) { //todo: we can use a document factory instead of switch case
            case DLPFILE: {
                DlpFileRecordDocumentBuilder dlpFileRecordDocumentBuilder = new DlpFileRecordDocumentBuilder();
                for (String[] record : records) {
                    createdDocuments.add(dlpFileRecordDocumentBuilder.createDlpFileRecordDocument(record));
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


    private String getMandatoryParamAsString(String paramName, String... params) throws Exception {
        for (String param : params) {
            final String[] splitParam = param.split(PARAM_DELIMITER);
            if (splitParam[0].toLowerCase().equals(paramName)) {
                return splitParam[1];
            }
        }

        final String errorMessage = String.format("Mandatory param %s was not provided.", paramName);
        logger.error(errorMessage);
        throw new Exception(errorMessage);
    }

    private void validateParams(long startTime, long endTime) throws Exception {
        if (!(startTime >= 0)) {
            throw new Exception(String.format("%s can't be negative! %s:%s", START_TIME_FIELD_NAME, START_TIME_FIELD_NAME, startTime));
        }
        if (!(startTime < endTime)) { //todo: maybe we can check that it's exactly 1 hour?
            throw new Exception(String.format("%s must be less than %s! %s:%s, %s:%s", START_TIME_FIELD_NAME, END_TIME_FIELD_NAME, START_TIME_FIELD_NAME, startTime, END_TIME_FIELD_NAME, endTime));
        }
        final long now = System.currentTimeMillis();
        if (!(endTime <= now)) {
            throw new Exception(String.format("%s can't be in the future! %s:%s, %s:%s", END_TIME_FIELD_NAME, END_TIME_FIELD_NAME, endTime, "now", now));
        }
    }

}
