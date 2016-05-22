package fortscale.utils.influxdb;

import fortscale.utils.influxdb.Exception.InfluxDBNetworkExcpetion;
import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import fortscale.utils.logging.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import retrofit.RetrofitError;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * InfluxDB java client for all CRUD operations
 */
public interface InfluxdbService {
    final int INFLUX_MAX_ATTEMPTS = 3;
    final int INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS = 2 * 1000;


    /**
     * query influxdb
     *
     * @param query - containing db name and command
     * @return query result
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    public QueryResult query(final Query query);

    /**
     * query influxdb
     *
     * @param query    - containing db name and command
     * @param timeUnit - of the result
     * @return query
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    public QueryResult query(final Query query, TimeUnit timeUnit);

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
     * @param retentionPolicy
     * @param point
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    public void write(final String database, final String retentionPolicy, final Point point);

    /**
     * write batch points to db. enables batch point writes if previously disables.
     *
     * @param batchPoints
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    public void batchWrite(final BatchPoints batchPoints);
    /**
     * create database if doesnt already exists
     *
     * @param name of the desired db
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    public void createDatabase(final String name) ;

    /**
     * delete database
     *
     * @param name of the db
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    public void deleteDatabase(final String name);


    /**
     * get all existing databases name on influx
     *
     * @return list of existing databases in influx
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    public List<String> describeDatabases();


    /**
     * creates primary db retention
     * @param retentionName retention name
     * @param dbName database name
     * @param retentionDuration ratation duration. for example 8w = 8 weeks
     * @param replication replication for cluster support
     */
    public void createDBRetention(String retentionName, String dbName, String retentionDuration, String replication);

    /**
     * check if influxdb is up and running
     * @return true if running, false otherwise
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS_MILISECONDS), exclude = {InterruptedException.class})
    public boolean isInfluxDBStarted();
}
