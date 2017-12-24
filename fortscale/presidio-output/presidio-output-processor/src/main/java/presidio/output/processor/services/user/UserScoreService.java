package presidio.output.processor.services.user;

import presidio.output.domain.records.alerts.AlertEnums.*;
import presidio.output.domain.records.users.User;

import java.util.Map;
import java.util.Set;

/**
 * Created by shays on 27/08/2017.
 */
public interface UserScoreService {

    void clearUserScoreForUsersThatShouldNotHaveScore(Set<String> excludedUsersIds);

    Map<String, UsersAlertData> calculateUserScores(int alertEffectiveDurationInDays);

}
