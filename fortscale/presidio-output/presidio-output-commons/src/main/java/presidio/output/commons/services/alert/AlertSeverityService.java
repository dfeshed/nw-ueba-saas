package presidio.output.commons.services.alert;

import presidio.output.domain.records.alerts.AlertEnums;

/**
 * Created by Efrat Noam on 11/16/17.
 */
public interface AlertSeverityService {

    Double getUserScoreContributionFromSeverity(AlertEnums.AlertSeverity severity);

    AlertEnums.AlertSeverity severity(double score);
}
