package presidio.output.commons.services.alert;

/**
 * Created by Efrat Noam on 11/16/17.
 */
public interface AlertSeverityService {

    Double getUserScoreContributionFromSeverity(AlertEnums.AlertSeverity severity);

    AlertEnumsSeverityService.UserScoreToSeverity getSeveritiesMap(double[] userScores);

    AlertEnums.AlertSeverity severity(double score);
}
