package presidio.output.commons.services.user;

/**
 * Created by barak_schuster on 12/4/17.
 */
public interface UserSeverityService {

    UserSeverityServiceImpl.UserScoreToSeverity getSeveritiesMap(double[] userScores);
}
