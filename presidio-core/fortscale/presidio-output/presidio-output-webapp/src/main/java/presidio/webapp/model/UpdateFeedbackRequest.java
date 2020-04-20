package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by efratn on 30/11/2017.
 */
public class UpdateFeedbackRequest {

    @JsonProperty("alertIds")
    private List<String> alertIds;

    @JsonProperty("feedback")
    AlertQueryEnums.AlertFeedback alertFeedback;

    public List<String> getAlertIds() {
        return alertIds;
    }

    public AlertQueryEnums.AlertFeedback getAlertFeedback() {
        return alertFeedback;
    }

    public void setAlertIds(List<String> alertIds) {
        this.alertIds = alertIds;
    }

    public void setAlertFeedback(AlertQueryEnums.AlertFeedback alertFeedback) {
        this.alertFeedback = alertFeedback;
    }
}
