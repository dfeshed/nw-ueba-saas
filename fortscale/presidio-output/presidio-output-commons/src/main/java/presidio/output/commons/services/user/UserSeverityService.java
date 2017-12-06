package presidio.output.commons.services.user;

import presidio.output.domain.records.users.User;

import java.util.List;

/**
 * Created by Efrat Noam on 12/4/17.
 */
public interface UserSeverityService {

    UserSeverityServiceImpl.UserScoreToSeverity getSeveritiesMap(boolean recalcUserScorePercentiles);

    /**
     * Update severities for specific list of users
     *
     * @param users          - list of users
     * @param persistChanges - if true -save updated users to DB. If false- only update the users on the list
     */
    void updateSeveritiesForUsersList(List<User> users, boolean persistChanges);

    /**
     * Iterate all users and re-calculate the getSeverity - read users from DB and update severities in DB
     */
    void updateSeverities();
}
