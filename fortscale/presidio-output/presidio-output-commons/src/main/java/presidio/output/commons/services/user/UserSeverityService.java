package presidio.output.commons.services.user;

/**
 * Created by Efrat Noam on 12/4/17.
 */
public interface UserSeverityService {

    UserSeverityServiceImpl.UserScoreToSeverity getSeveritiesMap(double[] userScores);
}
