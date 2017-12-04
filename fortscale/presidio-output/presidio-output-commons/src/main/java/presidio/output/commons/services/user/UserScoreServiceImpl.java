package presidio.output.commons.services.user;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.records.users.User;

/**
 * Created by efratn on 03/12/2017.
 */
public class UserScoreServiceImpl implements UserScoreService {

    private int percentThresholdCritical;
    private int percentThresholdHigh;
    private int percentThresholdMedium;

    @Autowired
    private AlertSeverityService alertSeverityService;

    @Override
    public void increaseUserScoreWithoutSaving(AlertEnums.AlertSeverity alertSeverity, User user) {
        double userScoreContribution = alertSeverityService.getUserScoreContributionFromSeverity(alertSeverity);
        double userScore = user.getScore();
        userScore += userScoreContribution;
        user.setScore(userScore);
    }

    /**
     * Calculate severities map
     *
     * @param userScores
     * @return map from score to severity
     */
    @Override
    public UserScoreServiceImpl.UserScoreToSeverity getSeveritiesMap(double[] userScores) {
        Percentile p = new Percentile();

        p.setData(userScores);

        double ceilScoreForLowSeverity = p.evaluate(percentThresholdMedium); //The maximum score that user score still considered low
        double ceilScoreForMediumSeverity = p.evaluate(percentThresholdHigh);//The maximum score that user score still considered medium
        double ceilScoreForHighSeverity = p.evaluate(percentThresholdCritical); //The maximum score that user score still considered high

        UserScoreServiceImpl.UserScoreToSeverity userScoreToSeverity = new UserScoreServiceImpl.UserScoreToSeverity(ceilScoreForLowSeverity, ceilScoreForMediumSeverity, ceilScoreForHighSeverity);


        return userScoreToSeverity;

    }

    public static class UserScoreToSeverity {
        private double ceilScoreForLowSeverity;
        private double ceilScoreForMediumSeverity;
        private double ceilScoreForHighSeverity;

        public UserScoreToSeverity(double ceilScoreForLowSeverity, double ceilScoreForMediumSeverity, double ceilScoreForHighSeverity) {
            this.ceilScoreForLowSeverity = ceilScoreForLowSeverity;
            this.ceilScoreForMediumSeverity = ceilScoreForMediumSeverity;
            this.ceilScoreForHighSeverity = ceilScoreForHighSeverity;
        }

        public UserSeverity getUserSeverity(double score) {
            if (score <= ceilScoreForLowSeverity) {
                return UserSeverity.LOW;
            } else if (score <= ceilScoreForMediumSeverity) {
                return UserSeverity.MEDIUM;
            } else if (score <= ceilScoreForHighSeverity) {
                return UserSeverity.HIGH;
            } else {
                return UserSeverity.CRITICAL;
            }
        }
    }
}
