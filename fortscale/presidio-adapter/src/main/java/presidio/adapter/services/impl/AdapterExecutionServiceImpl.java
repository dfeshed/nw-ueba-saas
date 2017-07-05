package presidio.adapter.services.impl;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.domain.core.AbstractAuditableDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.adapter.services.api.FetchService;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.services.CoreManagerService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AdapterExecutionServiceImpl implements PresidioExecutionService {
    private static Logger logger = LoggerFactory.getLogger(AdapterExecutionServiceImpl.class);

    private final CoreManagerService coreManagerService;
    private final FetchService fetchService;

    public AdapterExecutionServiceImpl(CoreManagerService coreManagerService, FetchService fetchService) {
        this.coreManagerService = coreManagerService;
        this.fetchService = fetchService;
    }

    @Override
    public void run(DataSource dataSource, Instant startDate, Instant endDate, Long fixedDuration) throws Exception {
        //todo: we need to consider doing the fetch & store at the same iteration
        logger.info("Start collector processing with params: data source:{}, from {}:{}, until {}:{}.",dataSource, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        final List<String[]> fetchedDocuments;
        try {
            fetchedDocuments = fetch(dataSource, startDate, endDate);
        } catch (Exception e) {
            logger.error("HEY USER!!! FETCH FAILED! params: data source:{}, from {}:{}, until {}:{}.",dataSource, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
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

    @Override
    public void clean(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {

    }

    @Override
    public void cleanAll(DataSource dataSource) throws Exception {

    }
}

