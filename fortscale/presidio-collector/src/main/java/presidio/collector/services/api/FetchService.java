package presidio.collector.services.api;


import presidio.collector.Datasource;
import presidio.sdk.api.domain.AbstractRecordDocument;

import java.util.List;

public interface FetchService {

    List<AbstractRecordDocument> fetch(Datasource datasource, long startime, long endtime) throws Exception;
}
