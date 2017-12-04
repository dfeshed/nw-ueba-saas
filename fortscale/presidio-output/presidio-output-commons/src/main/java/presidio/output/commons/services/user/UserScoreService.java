package presidio.output.commons.services.user;

import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;

/**
 * Created by efratn on 03/12/2017.
 */
public interface UserScoreService {

    //TODO- can be moved from here
    void increaseUserScoreWithoutSaving(AlertEnums.AlertSeverity alertSeverity, User user);

    UserScoreServiceImpl.UserScoreToSeverity getSeveritiesMap(double[] userScores);
}
