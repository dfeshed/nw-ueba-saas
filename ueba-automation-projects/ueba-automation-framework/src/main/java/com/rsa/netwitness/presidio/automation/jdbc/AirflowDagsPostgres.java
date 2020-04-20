package com.rsa.netwitness.presidio.automation.jdbc;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.jdbc.model.DagRunTable.DAG_RUN_TABLE;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.util.Lists.list;

public class AirflowDagsPostgres {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AirflowDagsPostgres.class.getName());

    private List<String> hourlyEntityFlowNames = list("ja3_hourly_ueba_flow", "sslSubject_hourly_ueba_flow", "userId_hourly_ueba_flow");

    public boolean allHourlyEntityFlowsExceeded(Instant endDate) {
        List<Optional<Instant>> maxDates = hourlyEntityFlowNames.stream().sequential()
                .map(this::getMaxExecutionDate)
                .collect(Collectors.toList());

        for (int i = 0; i < hourlyEntityFlowNames.size(); i++) {
            LOGGER.info("{dagId: MAX(execution_date)} = {" + hourlyEntityFlowNames.get(i) + ": " + maxDates.get(i) + "}");
        }
        LOGGER.info("All execution_dates should be after endDate=" + endDate);
        return maxDates.stream().map(e -> e.isPresent() && e.get().isAfter(endDate)).reduce(Boolean::logicalAnd).orElse(false);
    }


    private Optional<Instant> getMaxExecutionDate(String dagId) {
        String SQL_QUERY = "SELECT MAX(execution_date) AS max_execution_date" +
                " FROM " + DAG_RUN_TABLE +
                " WHERE dag_id = '" + dagId + "';";

        try (Connection con = PostgresAirflowConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return Optional.ofNullable(rs.getTimestamp(1).toInstant());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException");
        }

        return Optional.empty();
    }
}
