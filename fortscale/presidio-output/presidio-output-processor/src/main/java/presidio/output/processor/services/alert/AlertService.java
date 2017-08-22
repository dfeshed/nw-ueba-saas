package presidio.output.processor.services.alert;

import fortscale.domain.SMART.EntityEvent;
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
    Alert generateAlert(EntityEvent smart, User user);

    void save(List<Alert> alerts);
}
