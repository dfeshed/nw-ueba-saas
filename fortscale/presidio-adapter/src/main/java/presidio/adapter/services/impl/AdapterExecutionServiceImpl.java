package presidio.adapter.services.impl;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.domain.core.AbstractAuditableDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.adapter.services.api.FetchService;
import presidio.sdk.api.domain.ActiveDirectoryRawEvent;
import presidio.sdk.api.domain.AuthenticationRawEvent;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.FileRawEvent;
import presidio.sdk.api.services.CoreManagerService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AdapterExecutionServiceImpl implements PresidioExecutionService {
    private static Logger logger = LoggerFactory.getLogger(AdapterExecutionServiceImpl.class);

    private final CoreManagerService coreManagerService;
    private final FetchService fetchService;

    public AdapterExecutionServiceImpl() {

    }

    public AdapterExecutionServiceImpl(CoreManagerService coreManagerService, FetchService fetchService) {
        this.coreManagerService = coreManagerService;
        this.fetchService = fetchService;
    }

    @Override
    public void run(DataSource dataSource, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        //todo: we need to consider doing the fetch & store at the same iteration
        logger.info("Starting Adapter with params: data source: {}, {} : {}, {} : {}.",
                dataSource, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        String configurationPath = configureAdapterInstance(dataSource, startDate, endDate);
        logger.debug("finish configuring adapter. Configuration file path: {}", configurationPath);

        int processID = runAdapterInstance(dataSource);
        logger.debug("Adapter is now running. processID {}", processID);
    }

    private String configureAdapterInstance(DataSource dataSource, Instant startDate, Instant endDate) {
        logger.debug("Start configuring adapter. params: data source: {}, {} : {}, {} : {}.",
                dataSource, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
    }

    private int runAdapterInstance(DataSource dataSource) {
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
            case FILE:{
                for (String[] record : records) {
                    createdDocuments.add(new FileRawEvent(record));
                }
                break;
            }
            case ACTIVE_DIRECTORY:{
                for (String[] record : records) {
                    createdDocuments.add(new ActiveDirectoryRawEvent(record));
                }
                break;
            }
            case AUTHENTICATION:{
                for (String[] record : records) {
                    createdDocuments.add(new AuthenticationRawEvent(record));
                }
                break;
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

