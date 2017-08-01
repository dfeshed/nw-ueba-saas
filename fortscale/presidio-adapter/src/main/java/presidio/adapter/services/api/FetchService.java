package presidio.adapter.services.api;


import fortscale.common.general.Schema;

import java.time.Instant;
import java.util.List;

public interface FetchService {

    List<String[]> fetch(Schema schema, Instant startime, Instant endtime) throws Exception;
}
