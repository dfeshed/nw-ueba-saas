package fortscale.utils.influxdb;

import org.influxdb.dto.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * InfluxDB java client for all CRUD operations
 */
public interface InfluxdbService {
    int INFLUX_MAX_ATTEMPTS = 3;
    int INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS = 2 * 1000;


    /**
     * query influxdb
     *
     * @param query - containing db name and command
     * @return query result
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    QueryResult query(final Query query);

    /**
     * query influxdb
     *
     * @param query    - containing db name and command
     * @param timeUnit - of the result
     * @return query
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    QueryResult query(final Query query, TimeUnit timeUnit);

    /**
     * enable batch writes to influx
     *
     * @param actions               number of actions
     * @param flushDuration         number of TIMEUNIT (i.e. 10 seconds) before flush
     * @param flushDurationTimeUnit time unit of flush duration
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    void enableBatch(final int actions, final int flushDuration, final TimeUnit flushDurationTimeUnit);

    /**
     * disable batch
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    void disableBatch();

    /**
     * writhing a point to influxdb
     *
     * @param database        db name
     * @param retentionPolicy db retebtuib policy
     * @param point point two write
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    void write(final String database, final String retentionPolicy, final Point point);

    /**
     * write batch points to db. enables batch point writes if previously disables.
     *
     * @param batchPoints batch points to write
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    void batchWrite(final BatchPoints batchPoints);
    /**
     * create database if doesnt already exists
     *
     * @param name of the desired db
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    void createDatabase(final String name) ;

    /**
     * delete database
     *
     * @param name of the db
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    void deleteDatabase(final String name);


    /**
     * get all existing databases name on influx
     *
     * @return list of existing databases in influx
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    List<String> describeDatabases();


    /**
     * creates primary db retention
     * @param retentionName retention name
     * @param dbName database name
     * @param retentionDuration ratation duration. for example 8w = 8 weeks
     * @param replication replication for cluster support
     */
    void createDBRetention(String retentionName, String dbName, String retentionDuration, String replication);

    /**
     * check if influxdb is up and running
     * @return true if running, false otherwise
     */
    boolean isInfluxDBStarted();
}
