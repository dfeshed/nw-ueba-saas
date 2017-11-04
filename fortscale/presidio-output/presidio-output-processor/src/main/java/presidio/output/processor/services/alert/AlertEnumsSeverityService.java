package presidio.output.processor.services.alert;


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

}

