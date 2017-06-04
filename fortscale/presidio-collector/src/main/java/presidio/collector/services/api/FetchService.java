package presidio.collector.services.api;


import fortscale.common.general.Datasource;

import java.util.List;

public interface FetchService {

    List<String[]> fetch(Datasource datasource, long startime, long endtime) throws Exception;
}
