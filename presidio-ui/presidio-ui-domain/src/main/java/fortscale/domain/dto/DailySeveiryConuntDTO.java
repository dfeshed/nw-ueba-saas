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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DailySeveiryConuntDTO that = (DailySeveiryConuntDTO) o;

        return day == that.day;
    }

    @Override
    public int hashCode() {
        return (int) (day ^ (day >>> 32));
    }

    public void updateSeverity(Severity severity, Integer count) {
        if (count!=null && severity !=null) {
            for (SeveritiesCountDTO severityCountDto : this.getSeverities()) {
                if (severityCountDto.getSeverity().equals(severity)) {
                    severityCountDto.setCount(count);

                }
            }
        }
    }
}
