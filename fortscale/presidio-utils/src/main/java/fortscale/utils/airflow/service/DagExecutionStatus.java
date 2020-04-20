package fortscale.utils.airflow.service;

import fortscale.utils.airflow.message.DagState;
import fortscale.utils.time.TimeRange;

import java.time.Instant;
import java.util.List;

/**
 * Created by barak_schuster on 9/18/17.
 */
public class DagExecutionStatus {
    private String dagId;
    private Instant startInstant;
    private List<TimeRange> executionDates;
    private DagState executionDatesState;

    public DagExecutionStatus(String dagId, Instant startInstant, List<TimeRange> executionDates, DagState executionDatesState) {
        this.dagId = dagId;
        this.startInstant = startInstant;
        this.executionDates = executionDates;
        this.executionDatesState = executionDatesState;
    }

    public String getDagId() {
        return dagId;
    }

    public void setDagId(String dagId) {
        this.dagId = dagId;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    public List<TimeRange> getExecutionDates() {
        return executionDates;
    }

    public void setExecutionDates(List<TimeRange> executionDates) {
        this.executionDates = executionDates;
    }

    public DagState getExecutionDatesState() {
        return executionDatesState;
    }

    public void setExecutionDatesState(DagState executionDatesState) {
        this.executionDatesState = executionDatesState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DagExecutionStatus)) return false;

        DagExecutionStatus that = (DagExecutionStatus) o;

        if (dagId != null ? !dagId.equals(that.dagId) : that.dagId != null) return false;
        if (startInstant != null ? !startInstant.equals(that.startInstant) : that.startInstant != null) return false;
        if (executionDates != null ? !executionDates.equals(that.executionDates) : that.executionDates != null)
            return false;
        return executionDatesState == that.executionDatesState;
    }

    @Override
    public int hashCode() {
        int result = dagId != null ? dagId.hashCode() : 0;
        result = 31 * result + (startInstant != null ? startInstant.hashCode() : 0);
        result = 31 * result + (executionDates != null ? executionDates.hashCode() : 0);
        result = 31 * result + (executionDatesState != null ? executionDatesState.hashCode() : 0);
        return result;
    }
}
