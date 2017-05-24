package presidio.collector.services.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.collector.Datasource;
import presidio.collector.services.api.CollectorExecutionService;
import presidio.collector.services.api.FetchService;
import presidio.sdk.api.domain.AbstractRecordDocument;
import presidio.sdk.api.services.CoreManagerSdk;

import java.util.Arrays;
import java.util.List;


public class CollectorExecutionServiceImpl implements CollectorExecutionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int DATASOURCE_INDEX = 0;
    private static final int START_TIME_INDEX = 1;
    private static final int END_TIME_INDEX = 2;

    private final CoreManagerSdk coreManagerSdk;
    private final FetchService fetchService;

    public CollectorExecutionServiceImpl(CoreManagerSdk coreManagerSdk, FetchService fetchService) {
        this.coreManagerSdk = coreManagerSdk;
        this.fetchService = fetchService;
    }

    public void run(String... params) throws Exception {         //todo: we need to consider doing the fetch & store at the same iteration
        logger.info("Start csv processing with params: " + params);

        if (params.length < 3) {
            System.out.println();
            System.out.println("illegal input. need at least datasource, startime and endtime");
            System.out.println();
            return;
        }

        final Datasource dataSource = Datasource.createDataSource(params[DATASOURCE_INDEX]);
        final long startTime = Long.parseLong(params[START_TIME_INDEX]);
        final long endTime = Long.parseLong(params[END_TIME_INDEX]);
        final List<AbstractRecordDocument> fetchedDocuments;
        try {
            fetchedDocuments = fetch(dataSource, startTime, endTime);
        } catch (Exception e) {
            System.out.println(("HEY USER!!! FETCH FAILED! params: " + Arrays.toString(params)));
            //todo: how do we handle? alert the user probably?
            return;
        }

        final boolean storeSuccessful = store(fetchedDocuments);

        if (!storeSuccessful) {
            logger.error("store unsuccessful!!!");
            //todo: how do we handle?
        }

        logger.info("Finish csv processing");
    }

    private List<AbstractRecordDocument> fetch(Datasource dataSource, long startTime, long endTime) throws Exception {
        logger.info("Start fetch");
        final List<AbstractRecordDocument> fetchedDocuments = fetchService.fetch(dataSource, startTime, endTime); //todo: maybe the retry logic will be here?
        logger.info("finish fetch");
        return fetchedDocuments;
    }

    private boolean store(List<AbstractRecordDocument> fetchedDocuments) {
        logger.info("Start store");
        final boolean storeSuccessful = coreManagerSdk.store(fetchedDocuments);
        logger.info("finish store");
        return storeSuccessful;
    }
}
