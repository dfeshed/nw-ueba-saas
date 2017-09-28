package presidio.output.processor.services.user;

import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;

import java.util.List;

/**
 * Created by efratn on 22/08/2017.
 */
public interface UserService {
    User createUserEntity(String userId);

    void save(List<User> users);

    void setClassification(User user, List<String> classification);

    User findUserById(String userId);

    void setUserAlertData(User user, List<String> classification, List<String> indicators, AlertEnums.AlertSeverity alertSeverity);

    List<User> findUserByVendorUserIds(List<String> vendorUserId);

    /**
     * Recalculate all alerts related data on the user for the last X days (configurable).
     * Recalculating- user score, alerts count and classification
     *
     * @return
     */
    boolean updateAllUsersAlertData();
}
