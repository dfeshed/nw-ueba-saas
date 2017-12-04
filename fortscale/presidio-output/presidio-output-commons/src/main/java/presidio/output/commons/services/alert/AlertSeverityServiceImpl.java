package presidio.output.commons.services.alert;


import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import presidio.output.commons.services.user.UserScoreServiceImpl;
import presidio.output.domain.records.alerts.AlertEnums;

import java.util.Map;
import java.util.TreeMap;

public class AlertSeverityServiceImpl implements AlertSeverityService {

    private double criticalScore;
    private double highScore;
    private double midScore;

    private Map<AlertEnums.AlertSeverity, Double> alertSeverityToScoreContribution;

    public AlertSeverityServiceImpl(double criticalScore,
                                    double highScore,
                                    double midScore,
                                    double alertContributionCritical,
                                    double alertContributionHigh,
                                    double alertContributionMedium,
                                    double alertContributionLow) {
        this.criticalScore = criticalScore;
        this.highScore = highScore;
        this.midScore = midScore;

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



}

