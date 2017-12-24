package presidio.output.commons.services.user;

import presidio.output.domain.records.users.User;

import java.util.List;

/**
 * Created by Efrat Noam on 12/4/17.
 */
public interface UserSeverityService {

    UserSeverityServiceImpl.UserScoreToSeverity getSeveritiesMap(boolean recalcUserScorePercentiles);

    /**
     * Iterate all users and re-calculate the severities percentiles - read users from DB and update severities in DB
     */
    void updateSeverities();
}
