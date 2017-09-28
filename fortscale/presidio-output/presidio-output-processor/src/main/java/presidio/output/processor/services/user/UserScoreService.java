package presidio.output.processor.services.user;

import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by shays on 27/08/2017.
 */
public interface UserScoreService {

    /**
     * Iterate all users and re-calculate the severity - read users from DB and update severities in DB
     */
    void updateSeverities();

    void clearUserScoreForUsersThatShouldNotHaveScore(Set<String> excludedUsersIds);

    Map<String, UsersAlertData> calculateUserScores(int alertEffectiveDurationInDays);

    /**
     * Update severities for specific list of users
     *
     * @param users          - list of users
     * @param persistChanges - if true -save updated users to DB. If false- only update the users on the list
     */
    void updateSeveritiesForUsersList(List<User> users, boolean persistChanges);

    /**
     * Increasing the user score, depended on the alert severity. Update the user and persist
     *
     * @param alert
     */
    void increaseUserScoreWithoutSaving(AlertEnums.AlertSeverity alert, User user);

    Double getUserScoreContributionFromSeverity(AlertEnums.AlertSeverity severity);
}
