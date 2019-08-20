package com.rsa.netwitness.presidio.automation.jdbc;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskFailTable;
import com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskInstanceTable;

import java.sql.*;
import java.time.Instant;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskFailTable.*;
import static com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskInstanceTable.TASK_INSTANCE_TABLE;
import static org.assertj.core.api.Assertions.fail;


public class AirflowDbHelper {


    public List<AirflowTaskFailTable> fetchFailedTasks(Instant startTime) {
        String SQL_QUERY = "select * from " + AirflowTaskFailTable.TASK_FAIL_TABLE + " where " + AirflowTaskFailTable.START_DATE + " > '" + Timestamp.from(startTime) + "'";
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

    public List<AirflowTaskInstanceTable> fetchRetries(Instant startTime) {
        String SQL_QUERY = "select * from " + TASK_INSTANCE_TABLE + " where " + AirflowTaskInstanceTable.START_DATE + " > '" + Timestamp.from(startTime) + "'";
        List<AirflowTaskInstanceTable> airflowTaskFailTables = Lists.newLinkedList();

        try (Connection con = PostgresAirflowConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String taskId = rs.getString(AirflowTaskInstanceTable.TASK_ID);
                Instant executionDate = rs.getTimestamp(AirflowTaskInstanceTable.EXECUTION_DATE).toInstant();
                int tryNumber = rs.getInt(AirflowTaskInstanceTable.TRY_NUMBER);
                int maxTries = rs.getInt(AirflowTaskInstanceTable.MAX_TRIES);

                airflowTaskFailTables.add(new AirflowTaskInstanceTable(
                        taskId,
                        executionDate,
                        tryNumber,
                        maxTries
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException");
        }

        return airflowTaskFailTables;
    }

}
