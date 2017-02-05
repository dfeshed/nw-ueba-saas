package fortscale.domain.core.alert.analystfeedback;

import fortscale.domain.core.alert.AlertFeedback;

/**
 * Created by alexp on 02/02/17.
 */
public class RiskFeedback extends AnalystFeedback {

    private AlertFeedback alertFeedback;
    private Double scoreAfter;
    private Double scoreDelta;

    public RiskFeedback(String analystUserName, long updateDate, AlertFeedback alertFeedback, Double scoreAfter, Double scoreDelta) {
        super(analystUserName, updateDate);
        this.alertFeedback = alertFeedback;
        this.scoreAfter = scoreAfter;
        this.scoreDelta = scoreDelta;
    }

    public AlertFeedback getAlertFeedback() {
        return alertFeedback;
    }

    public void setAlertFeedback(AlertFeedback alertFeedback) {
        this.alertFeedback = alertFeedback;
    }

    public Double getScoreAfter() {
        return scoreAfter;
    }

    public void setScoreAfter(Double scoreAfter) {
        this.scoreAfter = scoreAfter;
    }

    public Double getScoreDelta() {
        return scoreDelta;
    }

    public void setScoreDelta(Double scoreDelta) {
        this.scoreDelta = scoreDelta;
    }
}
