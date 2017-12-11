package presidio.output.commons.services.alert;

import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;

import java.util.List;

/**
 * Created by efratn on 04/12/2017.
 */
public interface FeedbackService {

    void updateAlertFeedback(List<String> alertIds, AlertEnums.AlertFeedback feedback);
}
