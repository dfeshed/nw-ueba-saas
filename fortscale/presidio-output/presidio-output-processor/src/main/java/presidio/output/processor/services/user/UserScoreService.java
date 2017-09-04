package presidio.output.processor.services.user;

import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.users.User;

import java.util.List;

/**
 * Created by shays on 27/08/2017.
 */
public interface UserScoreService {

    /**
     * Iterate all users and re-calculate the severity - read users from DB and update severities in DB
     */
    void updateSeverities();

    /**
     * Update severities for specific list of users
     * @param users - list of users
     * @param persistChanges - if true -save updated users to DB. If false- only update the users on the list
     */
    void updateSeveritiesForUsersList(List<User> users, boolean persistChanges);

    /**
     * Increasing the user score, depended on the alert severity. Update the user and persist
     * @param alert
     */
    void increaseUserScoreWithoutSaving(Alert alert, User user);

    /**
     * Recalculate all user scores in the last X days
     * @return
     */
    boolean updateAllUsersScores();
}
