package presidio.adapter.services.api;


import fortscale.common.general.PresidioSchemas;

import java.time.Instant;
import java.util.List;

public interface FetchService {

    List<String[]> fetch(PresidioSchemas presidioSchemas, Instant startime, Instant endtime) throws Exception;
}
