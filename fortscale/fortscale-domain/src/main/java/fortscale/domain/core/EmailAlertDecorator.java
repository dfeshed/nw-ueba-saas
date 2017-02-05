package fortscale.domain.core;

import fortscale.domain.core.alert.Alert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avivs on 20/01/16.
 */
public class EmailAlertDecorator extends Alert {


    public String getStartDateShort() {
        return startDateShort;
    }

    public void setStartDateShort(String startDateShort) {
        this.startDateShort = startDateShort;
    }

    public String getStartDateLong() {
        return startDateLong;
    }

    public void setStartDateLong(String startDateLong) {
        this.startDateLong = startDateLong;
    }

    public String getEndDateShort() {
        return endDateShort;
    }

    public void setEndDateShort(String endDateShort) {
        this.endDateShort = endDateShort;
    }

    public String getEndDateLong() {
        return endDateLong;
    }

    public void setEndDateLong(String endDateLong) {
        this.endDateLong = endDateLong;
    }

    public List<EmailEvidenceDecorator> getEmailEvidences() {
        return emailEvidences;
    }

    public void setEmailEvidences(List<EmailEvidenceDecorator> emailEvidences) {
        this.emailEvidences = emailEvidences;
    }

    private String startDateShort;
    private String startDateLong;

    private String endDateShort;
    private String endDateLong;

    private List<EmailEvidenceDecorator> emailEvidences;

    public EmailAlertDecorator() {}

    public EmailAlertDecorator(Alert alert) {
        super(alert);

        emailEvidences = new ArrayList<>();
    }




}
