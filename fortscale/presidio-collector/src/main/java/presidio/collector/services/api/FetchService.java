package presidio.collector.services.api;


import fortscale.common.general.DataSource;

import java.util.List;

public interface FetchService {

    List<String[]> fetch(DataSource dataSource, long startime, long endtime) throws Exception;
}
