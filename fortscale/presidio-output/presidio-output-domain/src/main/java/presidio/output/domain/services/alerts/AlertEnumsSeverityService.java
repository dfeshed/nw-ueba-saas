package presidio.output.domain.services.alerts;


import presidio.output.domain.records.alerts.AlertEnums;

public class AlertEnumsSeverityService {

    private double criticalScore;
    private double highScore;
    private double midScore;
    private double lowScore;

    public AlertEnumsSeverityService(double criticalScore, double highScore, double midScore, double lowScore) {
        this.criticalScore = criticalScore;
        this.highScore = highScore;
        this.midScore = midScore;
        this.lowScore = lowScore;
    }


    public AlertEnums.AlertSeverity severity(double score) {
        if (lowScore <= score && score < midScore)
            return AlertEnums.AlertSeverity.LOW;
        if (midScore <= score && score < highScore)
            return AlertEnums.AlertSeverity.MEDIUM;
        if (highScore <= score && score < criticalScore)
            return AlertEnums.AlertSeverity.HIGH;
        return AlertEnums.AlertSeverity.CRITICAL;
    }

}

