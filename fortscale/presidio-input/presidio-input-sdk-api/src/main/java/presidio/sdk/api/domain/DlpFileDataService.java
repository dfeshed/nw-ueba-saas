package presidio.sdk.api.domain;

import java.util.List;


public interface DlpFileDataService {

    boolean store(List<DlpFileDataDocument> documents);

    List<DlpFileDataDocument> find(long startTime, long endTime);


}
