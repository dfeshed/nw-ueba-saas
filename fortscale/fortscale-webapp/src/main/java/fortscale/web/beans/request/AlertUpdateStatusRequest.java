package fortscale.web.beans.request;

import fortscale.domain.core.alert.AlertFeedback;
import fortscale.domain.core.alert.AlertStatus;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by alexp on 06/02/2017.
 */
public class AlertUpdateStatusRequest {
    private AlertFeedback feedback;
    private AlertStatus status;

    @NotNull
    @NotEmpty
    private String analystUserName;

    public AlertFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(AlertFeedback feedback) {
        this.feedback = feedback;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public String getAnalystUserName() {
        return analystUserName;
    }

    public void setAnalystUserName(String analystUserName) {
        this.analystUserName = analystUserName;
    }
}
