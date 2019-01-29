package fortscale.services;

import java.time.LocalDateTime;
import java.util.Map;

public interface NwInvestigateHelper {

    String getLinkToInvestigate(Object value, LocalDateTime startTime, LocalDateTime endTime);

    String getLinkToInvestigateHost(Object value);

    String getLinkToInvestigateProcess(Object value, Object hostName, Map<String,Object> map, Boolean isSourceProcess);
}
