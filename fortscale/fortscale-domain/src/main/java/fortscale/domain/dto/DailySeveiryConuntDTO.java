package fortscale.domain.dto;

import fortscale.domain.core.Severity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shays on 23/05/2016.
 */
public class DailySeveiryConuntDTO {
    private long day;
    private List<SeveritiesCountDTO> severities;

    public DailySeveiryConuntDTO() {

    }

    public DailySeveiryConuntDTO(long day) {
        this.day = day;
        severities = new ArrayList<>();
        for (Severity severity : Severity.values()){
            severities.add(new SeveritiesCountDTO(severity,0));
        }

    }

//    public DailySeveiryConuntDTO(long day, List<SeveritiesCountDTO> severities) {
//        this.day = day;
//        this.severities = severities;
//    }

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

    /**
     * Get the count of sevrity, incement it by 1
     * @param severity
     * @return return the updated SeveritiesCount (after increment)
     */
    public SeveritiesCountDTO incrementCountBySeverity(Severity severity){
        SeveritiesCountDTO  severitiesCount = severities.get(severity.ordinal());
        severitiesCount.increment();
        return  severitiesCount;

    }


}
