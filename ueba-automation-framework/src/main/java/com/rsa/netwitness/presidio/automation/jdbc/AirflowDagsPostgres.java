package com.rsa.netwitness.presidio.automation.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import static com.rsa.netwitness.presidio.automation.jdbc.model.DagRunTable.DAG_RUN_TABLE;
import static org.assertj.core.api.Assertions.fail;

public class AirflowDagsPostgres {

    private Instant getMaxExecutionDateJa3HourlyUebaFlow() {
        return getMaxExecutionDate("ja3_hourly_ueba_flow");
    }

    private Instant getMaxExecutionDateUserIdHourlyUebaFlow() {
        return getMaxExecutionDate("userId_hourly_ueba_flow");
    }

    private Instant getMaxExecutionDateSslSubjectHourlyUebaFlow() {
        return getMaxExecutionDate("sslSubject_hourly_ueba_flow");
    }


    private Instant getMaxExecutionDate(String dagId) {
        String SQL_QUERY = "SELECT MAX(execution_date) AS max_execution_date" +
                "FROM " + DAG_RUN_TABLE +
                "WHERE dag_id = '" + dagId + "';";

        try (Connection con = PostgresAirflowConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getTimestamp("max_execution_date").toInstant();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException");
        }

        return null;
    }
}
