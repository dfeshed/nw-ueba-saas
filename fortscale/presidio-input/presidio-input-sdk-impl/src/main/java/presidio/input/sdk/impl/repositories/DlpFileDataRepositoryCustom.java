package presidio.input.sdk.impl.repositories;


import presidio.sdk.api.domain.DlpFileDataDocument;

import java.util.List;

public interface DlpFileDataRepositoryCustom {

    List<DlpFileDataDocument> find(long startTime, long endTime);
}
