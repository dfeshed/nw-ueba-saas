package fortscale.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fortscale.domain.core.Alert;

/**
 * Created by shays on 18/05/2016.
 */
public class AlertWithUserScore {

    @JsonUnwrapped
    //JsonUnwrapped telling to jackson to take all the attributes under Alert
    //and put them directly under AlertWithUserScore, with the alert attribute.
    private Alert alert;
    private double score;

    public AlertWithUserScore() {


    }

    public AlertWithUserScore(Alert alert, double score) {
        this.alert = alert;
        this.score = score;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
