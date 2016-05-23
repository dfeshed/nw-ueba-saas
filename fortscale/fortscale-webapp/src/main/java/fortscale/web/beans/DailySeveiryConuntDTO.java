package fortscale.web.beans;

import java.util.List;

/**
 * Created by shays on 23/05/2016.
 */
public class DailySeveiryConuntDTO {
    private long day;
    private List<SeveritiesCountDTO> severities;

    public DailySeveiryConuntDTO() {
    }

    public DailySeveiryConuntDTO(long day, List<SeveritiesCountDTO> severities) {
        this.day = day;
        this.severities = severities;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public List<SeveritiesCountDTO> getSeverities() {
        return severities;
    }

    public void setSeverities(List<SeveritiesCountDTO> severities) {
        this.severities = severities;
    }
}
