package presidio.output.commons.services.alert;


import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.Map;
import java.util.TreeMap;

public class AlertEnumsSeverityService implements AlertSeverityService {

    private double criticalScore;
    private double highScore;
    private double midScore;
    private int percentThresholdCritical;
    private int percentThresholdHigh;
    private int percentThresholdMedium;
    private Map<AlertEnums.AlertSeverity, Double> alertSeverityToScoreContribution;

    public AlertEnumsSeverityService(double criticalScore,
                                     double highScore,
                                     double midScore,
                                     double alertContributionCritical,
                                     double alertContributionHigh,
                                     double alertContributionMedium,
                                     double alertContributionLow,
                                     int percentThresholdCritical,
                                     int percentThresholdHigh,
                                     int percentThresholdMedium) {
        this.criticalScore = criticalScore;
        this.highScore = highScore;
        this.midScore = midScore;

        this.percentThresholdCritical = percentThresholdCritical;
        this.percentThresholdHigh = percentThresholdHigh;
        this.percentThresholdMedium = percentThresholdMedium;

        alertSeverityToScoreContribution = new TreeMap<>();
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.CRITICAL, alertContributionCritical);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.HIGH, alertContributionHigh);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.MEDIUM, alertContributionMedium);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.LOW, alertContributionLow);

    }

    @Override
    public AlertEnums.AlertSeverity severity(double score) {
        if (criticalScore <= score) {
            return AlertEnums.AlertSeverity.CRITICAL;
        }
        if (highScore <= score && score < criticalScore) {
            return AlertEnums.AlertSeverity.HIGH;
        }
        if (midScore <= score && score < highScore) {
            return AlertEnums.AlertSeverity.MEDIUM;
        }
        return AlertEnums.AlertSeverity.LOW;
    }

    @Override
    public Double getUserScoreContributionFromSeverity(AlertEnums.AlertSeverity severity) {
        return this.alertSeverityToScoreContribution.get(severity);
    }

    /**
     * Calculate severities map
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

