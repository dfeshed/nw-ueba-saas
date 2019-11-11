package com.rsa.netwitness.presidio.automation.jdbc.model;

import java.time.Instant;

public class AirflowTaskInstanceTable {
    public static final String TASK_INSTANCE_TABLE = "task_instance";

    public static final String TASK_ID = "task_id";
    public static final String DAG_ID = "dag_id";
    public static final String EXECUTION_DATE = "execution_date";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String DURATION = "duration";
    public static final String STATE = "state";
    public static final String TRY_NUMBER = "try_number";
    public static final String HOSTNAME = "hostname";
    public static final String UNIXNAME = "unixname";
    public static final String JOB_ID = "job_id";
    public static final String POOL = "pool";
    public static final String PRIORITY_WEIGHT = "priority_weight";
    public static final String OPERATOR = "operator";
    public static final String QUEUED_DTTM = "queued_dttm";
    public static final String PID = "pid";
    public static final String MAX_TRIES = "max_tries";
    public static final String EXECUTOR_CONFIG = "executor_config";

    public final String taskId;
    public final Instant executionDate;
    public final int tryNumber;
    public final int maxTries;
    public final String dagId;
    public final Instant startDate;
    public final Instant endDate;
    public final String state;


    public AirflowTaskInstanceTable(String dagId, String taskId, Instant executionDate, Instant startDate,
                                    Instant endDate, String state, int tryNumber, int maxTries) {
        this.dagId = dagId;
        this.taskId = taskId;
        this.executionDate = executionDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.tryNumber = tryNumber;
        this.maxTries = maxTries;
    }

    @Override
    public String toString() {
        return "AirflowTaskInstanceTable{" +
                "taskId='" + taskId + '\'' +
                ", executionDate=" + executionDate +
                ", tryNumber=" + tryNumber +
                ", maxTries=" + maxTries +
                ", dagId='" + dagId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", state='" + state + '\'' +
                '}';
    }

}


