package fortscale.utils.airflow;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Created by barak_schuster on 9/13/17.
 */
public class DagToExecutionDates {
    @JsonProperty("dag_id")
    private String dagId;
    @JsonProperty("execution_dates")
    private List<Instant> executionDates;

    public DagToExecutionDates() {
    }

    public String getDagId() {
        return dagId;
    }

    public void setDagId(String dagId) {
        this.dagId = dagId;
    }

    public List<Instant> getExecutionDates() {
        return executionDates;
    }

    public void setExecutionDates(List<Instant> executionDates) {
        this.executionDates = executionDates;
    }
}
