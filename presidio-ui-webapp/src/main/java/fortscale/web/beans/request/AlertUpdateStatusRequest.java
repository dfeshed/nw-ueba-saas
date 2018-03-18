package fortscale.web.beans.request;

import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.AlertStatus;

/**
 * Created by alexp on 06/02/2017.
 */
public class AlertUpdateStatusRequest {
    private AlertFeedback feedback;
    private AlertStatus status;

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
}
