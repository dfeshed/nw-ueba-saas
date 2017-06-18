package presidio.collector.services.api;


import fortscale.common.general.DataSource;

import java.time.Instant;
import java.util.List;

public interface FetchService {

    List<String[]> fetch(DataSource dataSource, Instant startime, Instant endtime) throws Exception;
}
