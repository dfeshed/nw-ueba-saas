package com.rsa.netwitness.presidio.automation.jdbc;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskFailTable;

import java.sql.*;
import java.time.Instant;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskFailTable.*;
import static org.assertj.core.api.Assertions.fail;


public class AirflowDbHelper {


    public List<AirflowTaskFailTable> fetchFailedTasks(Instant startTime) {
        String SQL_QUERY = "select * from " + TASK_FAIL_TABLE + " where " + START_DATE + " > '" + Timestamp.from(startTime) + "'";
        List<AirflowTaskFailTable> airflowTaskFailTables = Lists.newLinkedList();

        try (Connection con = PostgresAirflowConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt(ID);
                String taskId = rs.getString(TASK_ID);
                String dagId = rs.getString(DAG_ID);
                Instant executionDate = rs.getTimestamp(EXECUTION_DATE).toInstant();
                Instant startDate = rs.getTimestamp(START_DATE).toInstant();
                Instant endDate = rs.getTimestamp(END_DATE).toInstant();
                int duration = rs.getInt(DURATION);

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

}
