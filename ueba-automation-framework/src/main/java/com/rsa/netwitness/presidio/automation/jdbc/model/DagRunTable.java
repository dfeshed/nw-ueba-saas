package com.rsa.netwitness.presidio.automation.jdbc.model;

import java.time.Instant;

public class DagRunTable {
    public static final String DAG_RUN_TABLE = "dag_run";

    public final int id;
    public final String dagId;
    public final Instant executionDate;
    public final String state;
    public final String runId;
    public final boolean externalTrigger;
    public final Instant startDate;
    public final Instant endDate;

    public DagRunTable(int id, String dagId, Instant executionDate, String state, String runId, boolean externalTrigger, Instant startDate, Instant endDate) {
        this.id = id;
        this.dagId = dagId;
        this.executionDate = executionDate;
        this.state = state;
        this.runId = runId;
        this.externalTrigger = externalTrigger;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "DagRunTable{" +
                "id=" + id +
                ", dagId='" + dagId + '\'' +
                ", executionDate=" + executionDate +
                ", state='" + state + '\'' +
                ", runId='" + runId + '\'' +
                ", externalTrigger=" + externalTrigger +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

}
