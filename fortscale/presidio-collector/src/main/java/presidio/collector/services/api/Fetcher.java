package presidio.collector.services.api;


import fortscale.common.general.DataSource;

import java.time.Instant;
import java.util.List;

public interface Fetcher {

    //todo: we need to consider if we define fetchers's source in spring configuration
    //todo: or we allow some more params (maybe as an additionalParams map?)
    List<String[]> fetch(DataSource dataSource, Instant startTime, Instant endTime) throws Exception;

}
