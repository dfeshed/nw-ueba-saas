package presidio.output.processor.services.alert;

import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.users.User;

import java.util.List;

/**
 * Created by efratn on 24/07/2017.
 */
public interface AlertService {

    /**
     * Convert the received smarts into alerts and persist them
     * @param smart - ADE SMART to be converted into Presidio Alert
     * @param user- user entity related to the specified alert
     */
    Alert generateAlert(SmartRecord smart, User user, int smartThresholdScoreForCreatingAlert);

    void save(List<Alert> alerts);
}
