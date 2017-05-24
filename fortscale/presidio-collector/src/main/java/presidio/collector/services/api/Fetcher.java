package presidio.collector.services.api;

import presidio.collector.Datasource;
import presidio.sdk.api.domain.AbstractRecordDocument;

import java.util.List;

public interface Fetcher {

    //todo: we need to consider if we define fetchers's source in spring configuration
    //todo: or we allow some more params (maybe as an additionalParams map?)
    List<AbstractRecordDocument> fetch(Datasource datasource, long startTime, long endTime) throws Exception;

}
