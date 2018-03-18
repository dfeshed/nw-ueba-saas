package fortscale.domain.core.alert.analystfeedback;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.Severity;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by alexp on 02/02/17.
 */
@JsonTypeName(AnalystRiskFeedback.ANALYST_RISK_FEEDBACK_TYPE)
public class AnalystRiskFeedback extends AnalystFeedback {
    public static final String ANALYST_RISK_FEEDBACK_TYPE = "AnalystRiskFeedback";

    private static final String ALERT_FEEDBACK_FIELD = "alertFeedback";
    private static final String USER_SCORE_AFTER_FIELD = "userScoreAfter";
    private static final String USER_SCORE_SEVERITY_AFTER = "userScoreSeverityAfter";
    private static final String SCORE_DELTA_FIELD = "scoreDelta";

    @Field(ALERT_FEEDBACK_FIELD)
    private AlertFeedback alertFeedback;
    @Field(USER_SCORE_AFTER_FIELD)
    private Double userScoreAfter;
    @Field(USER_SCORE_SEVERITY_AFTER)
    private Severity userScoreSeverityAfter;
    @Field(SCORE_DELTA_FIELD)
    private Double scoreDelta;

    public AnalystRiskFeedback() {
    }

    /**
     *
     * @param analystUserName
     * @param alertFeedback
     * @param userScoreBefore
     * @param userScoreAfter
     * @param userScoreSeverityAfter
     * @param modifiedAt
     * @param alertId
     */
    public AnalystRiskFeedback(String analystUserName, AlertFeedback alertFeedback,
                               double userScoreBefore,double userScoreAfter,
                               Severity userScoreSeverityAfter,
                               Long modifiedAt, String alertId) {
        super(analystUserName, modifiedAt,alertId);
        this.alertFeedback = alertFeedback;
        this.userScoreAfter = userScoreAfter;
        this.userScoreSeverityAfter = userScoreSeverityAfter;
        // User score contribution
        this.scoreDelta = userScoreAfter -userScoreBefore;
    }

    public AlertFeedback getAlertFeedback() {
        return alertFeedback;
    }

    public void setAlertFeedback(AlertFeedback alertFeedback) {
        this.alertFeedback = alertFeedback;
    }

    public Double getUserScoreAfter() {
        return userScoreAfter;
    }

    public void setUserScoreAfter(Double userScoreAfter) {
        this.userScoreAfter = userScoreAfter;
    }

    public Double getScoreDelta() {
        return scoreDelta;
    }

    public void setScoreDelta(Double scoreDelta) {
        this.scoreDelta = scoreDelta;
    }

    public Severity getUserScoreSeverityAfter() {
        return userScoreSeverityAfter;
    }

    public void setUserScoreSeverityAfter(Severity userScoreSeverityAfter) {
        this.userScoreSeverityAfter = userScoreSeverityAfter;
    }
}