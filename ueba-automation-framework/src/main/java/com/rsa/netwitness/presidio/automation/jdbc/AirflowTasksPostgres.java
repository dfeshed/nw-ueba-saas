package com.rsa.netwitness.presidio.automation.jdbc;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskFailTable;
import com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskInstanceTable;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskInstanceTable.TASK_INSTANCE_TABLE;
import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;
import static org.assertj.core.api.Assertions.fail;


public class AirflowTasksPostgres {

    // by task execution time
    public List<AirflowTaskInstanceTable> fetchFailedStateTasks(Instant startTime, Instant endTime) {
        String SQL_QUERY = "select * from " + TASK_INSTANCE_TABLE +
                " where " + AirflowTaskInstanceTable.START_DATE + " > '" + Timestamp.from(startTime) + "'" +
                " and " + AirflowTaskInstanceTable.END_DATE + " < '" + Timestamp.from(endTime) + "'" +
                " and " + AirflowTaskInstanceTable.STATE + " = 'failed'";

        return fetchTasks(SQL_QUERY);
    }

    // by task execution time
    public List<AirflowTaskFailTable> fetchAllFailedAttempts(Instant startTime, Instant endTime) {
        String SQL_QUERY = "select * from " + AirflowTaskFailTable.TASK_FAIL_TABLE +
                " where " + AirflowTaskFailTable.START_DATE + " > '" + Timestamp.from(startTime) + "'" +
                " and " + AirflowTaskFailTable.END_DATE + " < '" + Timestamp.from(endTime) + "'";

        return fetchTaskFailTable(SQL_QUERY);
    }

    // by event time
    public List<AirflowTaskFailTable> fetchAllFailedAttempts(Instant execution_date) {
        String SQL_QUERY = "select * from " + AirflowTaskFailTable.TASK_FAIL_TABLE +
                " where " + AirflowTaskFailTable.EXECUTION_DATE + " < '" + Timestamp.from(execution_date) + "'";

        return fetchTaskFailTable(SQL_QUERY);
    }

    private List<AirflowTaskFailTable> fetchTaskFailTable(String SQL_QUERY) {

        List<AirflowTaskFailTable> airflowTaskFailTables = Lists.newLinkedList();

        try (Connection con = PostgresAirflowConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt(AirflowTaskFailTable.ID);
                String taskId = rs.getString(AirflowTaskFailTable.TASK_ID);
                String dagId = rs.getString(AirflowTaskFailTable.DAG_ID);
                Instant executionDate = rs.getTimestamp(AirflowTaskFailTable.EXECUTION_DATE).toInstant();
                Instant startDate = rs.getTimestamp(AirflowTaskFailTable.START_DATE).toInstant();
                Instant endDate = rs.getTimestamp(AirflowTaskFailTable.END_DATE).toInstant();
                int duration = rs.getInt(AirflowTaskFailTable.DURATION);

                airflowTaskFailTables.add(new AirflowTaskFailTable(
                        id,
                        taskId,
                        dagId,
                        executionDate,
                        startDate,
                        endDate,
                        duration
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException");
        }

        return airflowTaskFailTables;
    }

    public List<AirflowTaskInstanceTable> fetchRetries(Instant startTime, Instant endTime) {
        String SQL_QUERY = "select * from " + TASK_INSTANCE_TABLE +
        " where " + AirflowTaskFailTable.START_DATE + " > '" + Timestamp.from(startTime) + "'" +
                " and " + AirflowTaskFailTable.END_DATE + " < '" + Timestamp.from(endTime) + "'";

        return fetchTasks(SQL_QUERY);
    }

    public List<AirflowTaskInstanceTable> fetchTaskDetails(String dagId, String taskId, Instant startTime) {
        String SQL_QUERY = "select * from " + TASK_INSTANCE_TABLE
                + " where " + AirflowTaskInstanceTable.START_DATE + " > '" + Timestamp.from(startTime) + "'"
                + " and " + AirflowTaskInstanceTable.DAG_ID + "='" + dagId + "'"
                + " and " + AirflowTaskInstanceTable.TASK_ID + "='" + taskId + "'";

        return fetchTasks(SQL_QUERY);
    }


    public Optional<Instant> getFirstSucceededExecutionDate(String dagId, String taskId) {
        String SQL_QUERY = "select min(execution_date) as execution_date "
                + " from " + TASK_INSTANCE_TABLE
                + " where " + AirflowTaskInstanceTable.STATE + " = 'success'"
                + " and " + AirflowTaskInstanceTable.DAG_ID + "='" + dagId + "'"
                + " and " + AirflowTaskInstanceTable.TASK_ID + "='" + taskId + "'";

        List<Timestamp> result = fetchColumn(SQL_QUERY, Timestamp.class);
        if (result.isEmpty()) return Optional.empty();
        return Optional.ofNullable(getOrNull(result.get(0), Timestamp::toInstant));
    }

    private List<AirflowTaskInstanceTable> fetchTasks(String SQL_QUERY) {
        List<AirflowTaskInstanceTable> tasks = Lists.newLinkedList();

        try (Connection con = PostgresAirflowConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {
            tasks.addAll(buildTasks(rs));
        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException");
        }
        return tasks;
    }

    private <T>List<T> fetchColumn(String SQL_QUERY, Class<T> type){
        List<T> result = Lists.newLinkedList();
        try (Connection con = PostgresAirflowConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getObject(1, type));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException");
        }
        return result;
    }

    private List<AirflowTaskInstanceTable> buildTasks(ResultSet rs) throws SQLException {
        List<AirflowTaskInstanceTable> tasks = Lists.newLinkedList();

        while (rs.next()) {
            String dagId = rs.getString(AirflowTaskInstanceTable.DAG_ID);
            String taskId = rs.getString(AirflowTaskInstanceTable.TASK_ID);
            Instant executionDate = getOrNull(rs.getTimestamp(AirflowTaskInstanceTable.EXECUTION_DATE), Timestamp::toInstant);
            Instant startDate = getOrNull(rs.getTimestamp(AirflowTaskInstanceTable.START_DATE), Timestamp::toInstant);
            Instant endDate = getOrNull(rs.getTimestamp(AirflowTaskInstanceTable.END_DATE), Timestamp::toInstant);
            String state = rs.getString(AirflowTaskInstanceTable.STATE);
            int tryNumber = rs.getInt(AirflowTaskInstanceTable.TRY_NUMBER);
            int maxTries = rs.getInt(AirflowTaskInstanceTable.MAX_TRIES);

            tasks.add(new AirflowTaskInstanceTable(
                    dagId,
                    taskId,
                    executionDate,
                    startDate,
                    endDate,
                    state,
                    tryNumber,
                    maxTries
            ));
        }
        return tasks;
    }
}
