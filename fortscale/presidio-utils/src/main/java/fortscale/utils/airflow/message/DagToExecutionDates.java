package fortscale.utils.airflow.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.time.TimeRange;

import java.time.Instant;
import java.util.List;

/**
 * POJO that describes what are all the execution dates for given dag_id
 * Created by barak_schuster on 9/13/17.
 */
public class DagToExecutionDates {
    @JsonProperty("dag_id")
    private String dagId;
    @JsonProperty("execution_dates")
    private List<TimeRange> executionDates;

    @JsonProperty("start_date")
    private Instant startDate;

    public DagToExecutionDates() {
    }

    public String getDagId() {
        return dagId;
    }

    public void setDagId(String dagId) {
        this.dagId = dagId;
    }

    public  List<TimeRange> getExecutionDates() {
        return executionDates;
    }

    public void setExecutionDates( List<TimeRange> executionDates) {
        this.executionDates = executionDates;
    }

    /**
     *
     * @return the start date of the whole dag
     */
    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }
}
