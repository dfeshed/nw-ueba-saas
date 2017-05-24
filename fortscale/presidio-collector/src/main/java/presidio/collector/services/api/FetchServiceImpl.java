package presidio.collector.services.api;

import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import presidio.collector.Datasource;
import presidio.sdk.api.domain.AbstractRecordDocument;
import presidio.sdk.api.domain.DlpFileRecordDocumentBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FetchServiceImpl implements FetchService {
    private final Logger logger = Logger.getLogger(FetchServiceImpl.class);

    private Map<Datasource, Fetcher> fetchers;

    public FetchServiceImpl(Map<Datasource, Fetcher> fetchers) {
        this.fetchers = fetchers;
    }

    @Override
    public List<AbstractRecordDocument> fetch(Datasource datasource, long startTime, long endTime) throws Exception {
        logger.info("fetching datasource {} from start time {}[{}] to end time {}[{}].", datasource, new DateTime(startTime), startTime, new DateTime(endTime), endTime); //todo: can we have timezone issues?
        final Fetcher fetcher = fetchers.get(datasource);
        if (fetcher == null) {
            logger.error("There's no fetcher for datasource {}. Supported datasources are {}", fetchers.keySet());
            throw new Exception("Unsupported datasource: " + datasource);
        }

        final List<String[]> fetchResults;
        try {
            fetchResults = fetcher.fetch(datasource, startTime, endTime);
        } catch (Exception e) {
            logger.warn("fetch failed and we don't retry for now");
            //todo: how do we handle? maybe retry?
            throw e;
        }

        return createDocuments(datasource, fetchResults);
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

}


