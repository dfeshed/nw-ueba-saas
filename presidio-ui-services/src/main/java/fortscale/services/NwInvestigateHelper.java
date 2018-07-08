package fortscale.services;

import java.time.LocalDateTime;

public interface NwInvestigateHelper {

    String getLinkToInvestigate(Object value, LocalDateTime startTime, LocalDateTime endTime);
}
