package fortscale.web.beans;

import java.util.List;

/**
 * Created by shays on 23/05/2016.
 */
public class DailySeveiryConuntDTO {
    private long day;
    private List<SeveritiesCount> severities;

    public DailySeveiryConuntDTO() {
    }

    public DailySeveiryConuntDTO(long day, List<SeveritiesCount> severities) {
        this.day = day;
        this.severities = severities;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public List<SeveritiesCount> getSeverities() {
        return severities;
    }

    public void setSeverities(List<SeveritiesCount> severities) {
        this.severities = severities;
    }
}
