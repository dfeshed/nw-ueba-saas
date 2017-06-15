package presidio.input.sdk.impl.repositories;


import presidio.sdk.api.domain.DlpFileDataDocument;

import java.time.Instant;
import java.util.List;

public interface DlpFileDataRepositoryCustom {

    List<DlpFileDataDocument> find(Instant startTime, Instant endTime);

    int clean(Instant startTime, Instant endTime);


}
