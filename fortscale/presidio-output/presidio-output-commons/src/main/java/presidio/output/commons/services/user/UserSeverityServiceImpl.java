package presidio.output.commons.services.user;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import presidio.output.domain.records.users.UserSeverity;

/**
 * Created by efrat Noam on 12/4/17.
 */
public class UserSeverityServiceImpl implements UserSeverityService {

    private int percentThresholdCritical;
    private int percentThresholdHigh;
    private int percentThresholdMedium;

    public UserSeverityServiceImpl(int percentThresholdCritical,
                                int percentThresholdHigh,
                                int percentThresholdMedium) {
        this.percentThresholdCritical = percentThresholdCritical;
        this.percentThresholdHigh = percentThUserScoreServiceresholdHigh;
        this.percentThresholdMedium = percentThresholdMedium;
    }


    /**
     * Calculate severities map which defines the right user severity per user score calculated according to percentiles
     *
     * @param userScores
     * @return map from score to severity
     */
    @Override
    public UserScoreToSeverity getSeveritiesMap(double[] userScores) {
        Percentile p = new Percentile();

        p.setData(userScores);

        double ceilScoreForLowSeverity = p.evaluate(percentThresholdMedium); //The maximum score that user score still considered low
        double ceilScoreForMediumSeverity = p.evaluate(percentThresholdHigh);//The maximum score that user score still considered medium
        double ceilScoreForHighSeverity = p.evaluate(percentThresholdCritical); //The maximum score that user score still considered high

        UserScoreToSeverity userScoreToSeverity = new UserScoreToSeverity(ceilScoreForLowSeverity, ceilScoreForMediumSeverity, ceilScoreForHighSeverity);


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
