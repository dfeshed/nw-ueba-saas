package com.rsa.netwitness.presidio.automation.jdbc.model;

import java.time.Instant;

public class AirflowTaskFailTable {
    public static final String TASK_FAIL_TABLE = "task_fail";

    public static final String ID = "id";
    public static final String TASK_ID = "task_id";
    public static final String DAG_ID = "dag_id";
    public static final String EXECUTION_DATE = "execution_date";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String DURATION = "duration";

    public final int id;
    public final String taskId;
    public final String dagId;
    public final Instant executionDate;
    public final Instant startDate;
    public final Instant endDate;
    public final int duration;

    public AirflowTaskFailTable(int id, String taskId, String dagId,
                                Instant executionDate, Instant startDate,
                                Instant endDate, int duration) {
        this.id = id;
        this.taskId = taskId;
        this.dagId = dagId;
        this.executionDate = executionDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
    }


    @Override
    public String toString() {
        return "[id=" + id +
                ", taskId='" + taskId + '\'' +
                ", dagId='" + dagId + '\'' +
                ", executionDate=" + executionDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", duration=" + duration +
                ']';
    }

}
