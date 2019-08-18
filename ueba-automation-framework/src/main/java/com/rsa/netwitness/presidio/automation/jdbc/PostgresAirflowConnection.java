package com.rsa.netwitness.presidio.automation.jdbc;

import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.rsa.netwitness.presidio.automation.context.PostgresConfig.POSTGRES_PROPERTIES;

class PostgresAirflowConnection {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PostgresAirflowConnection.class.getName());
    private static final Lazy<HikariDataSource> ds = new Lazy<>();

    private static Supplier<HikariDataSource> initDataSource = () -> {
        LOGGER.info("Init postgres Airflow db connection pool.");
        HikariConfig config =  new HikariConfig();
        config.setJdbcUrl(POSTGRES_PROPERTIES.connectionURL());
        config.setUsername(POSTGRES_PROPERTIES.username());
        config.setPassword(POSTGRES_PROPERTIES.password());

        config.setMaximumPoolSize(3);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // We will wait for 15 seconds to get a connection from the pool.
        // Default is 30, but it shouldn't be taking that long.
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15)); // 15000

        // If a connection is not returned within 10 seconds, it's probably safe to assume it's been leaked.
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(10)); // 10000
        return new HikariDataSource(config);
    };

    private PostgresAirflowConnection() { }

    static Connection getConnection() throws SQLException {
        return ds.getOrCompute(initDataSource).getConnection();
    }


}
