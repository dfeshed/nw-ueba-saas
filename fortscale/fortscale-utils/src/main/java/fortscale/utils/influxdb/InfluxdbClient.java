package fortscale.utils.influxdb;

import fortscale.utils.influxdb.Exception.InfluxDBNetworkExcpetion;
import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import retrofit.RetrofitError;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * InfluxDB java client for all CRUD operations
 */
public class InfluxdbClient {
    private static Logger logger = LoggerFactory.getLogger(InfluxdbClient.class);
    private InfluxDB influxDB;
    private String influxdbIp;
    private String influxdbPort;
    private String influxdbUsername;
    private String influxdbPassword;
    private final int INFLUX_MAX_ATTEMPTS = 3;
    private final int INFLUX_DELAY_BETWEEN_ATTEMPTS = 10000;
    private static boolean isBatchEnabled;
    private int batchActions;
    private int batchFlushInterval;

    /**
     * InfluxdbClient C'tor
     * @param influxdbIP influxdb ip
     * @param influxdbPort influxdb port
     * @param logLevel rest api log level
     * @param readTimeout timeout for read queries
     * @param writeTimeout timeout for write operations
     * @param connectTimeout timeout for connect
     * @param batchActions number of points written in one batch. batch disabled by default, and enabled in the moment you'll write your first batch
     * @param batchFlushInterval batch flush interval in seconds
     */
    public InfluxdbClient(String influxdbIP, String influxdbPort, String logLevel, long readTimeout, long writeTimeout, long connectTimeout,int batchActions,int batchFlushInterval) {
        this.isBatchEnabled = false;
        this.influxdbIp = influxdbIP;
        this.influxdbPort = influxdbPort;
        this.influxdbUsername = "admin";
        this.influxdbPassword = "";
        this.influxDB = InfluxDBFactory.connect(String.format("http://%s:%s", this.influxdbIp, this.influxdbPort), this.influxdbUsername, this.influxdbPassword);
        this.influxDB.setReadTimeout(readTimeout, TimeUnit.SECONDS);
        this.influxDB.setWriteTimeout(writeTimeout, TimeUnit.SECONDS);
        this.influxDB.setConnectTimeout(connectTimeout, TimeUnit.SECONDS);
        this.influxDB.setLogLevel(InfluxDB.LogLevel.valueOf(logLevel)); //influxdb restApi logLevl
        this.batchActions=batchActions;
        this.batchFlushInterval=batchFlushInterval;
    }

    /**
     * query influxdb
     * @param query - containing db name and command
     * @return query result
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public QueryResult query(final Query query) {
        QueryResult response = null;
        try {
            logger.debug(String.format("EXECUTING: influxdb query: %s", query.getCommand()));
            Instant start = Instant.now();
            response = this.influxDB.query(query);
            Instant end = Instant.now();
            logger.debug(String.format("FINISHED: finished executing query %s , duration: %s", query.getCommand(), Duration.between(start, end).toString()));
        } catch (Exception e) {
            String errCmd = String.format("query: %s", query.getCommand());
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
        return response;
    }

    /**
     * query influxdb
     * @param query - containing db name and command
     * @param timeUnit - of the result
     * @return query
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public QueryResult query(final Query query, TimeUnit timeUnit) {
        QueryResult response = null;
        try {
            logger.debug(String.format("EXECUTING: influxdb query: %s", query.getCommand()));
            Instant start = Instant.now();
            response = this.influxDB.query(query, timeUnit);
            Instant end = Instant.now();
            logger.debug(String.format("FINISHED: finished executing query %s , duration: %s", query.getCommand(), Duration.between(start, end).toString()));
        } catch (Exception e) {
            String errCmd = String.format("query: %s", query.getCommand());
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
        return response;
    }

    /**
     * enable batch writes to influx
     * @param actions number of actions
     * @param flushDuration number of TIMEUNIT (i.e. 10 seconds) before flush
     * @param flushDurationTimeUnit  time unit of flush duration
     * @return influxdb object for continues work
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public InfluxDB enableBatch(final int actions, final int flushDuration, final TimeUnit flushDurationTimeUnit) {
        try {
            logger.debug(String.format("EXECUTING: influxdb enableBatch actions: %d , flushDuration: %d, flushDurationTimeUnit: %s", actions, flushDuration, flushDurationTimeUnit.name()));
            return this.influxDB.enableBatch(actions, flushDuration, flushDurationTimeUnit);
        } catch (Exception e) {
            String errCmd = String.format("enableBatch actions: %d, flushduration: %d TimeUnit: %s", actions, flushDuration, flushDurationTimeUnit.name());
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
    }

    /**
     * diable batch
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public void disableBatch() {
        try {
            logger.debug("EXECUTING: influxdb disableBatch");
            this.influxDB.disableBatch();
        } catch (Exception e) {
            String errCmd = "disableBatch";
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
    }

    /**
     * writeing a point to influxdb
     * @param database db name
     * @param retentionPolicy
     * @param point
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public void write(final String database, final String retentionPolicy, final Point point) {
        try {
            logger.debug(String.format("EXECUTING: influxdb write: database: %s, retention: %s, point: %s", database, retentionPolicy, point.toString()));
            this.influxDB.write(database, retentionPolicy, point);
        } catch (Exception e) {
            String errCmd = String.format("write db: %s, retention: %s, point: %s", database, retentionPolicy, point.toString());
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
    }

    /**
     * write batch points to db. enables batch point writes if previously disables.
     * @param batchPoints
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public void write(final BatchPoints batchPoints) {
        try {
            logger.debug(String.format("EXECUTING: influxdb batch write: %s", batchPoints.toString()));
            logger.info("EXECUTING: influxdb batch write for {} objects",batchPoints.getPoints().size());
            if (this.isBatchEnabled)
                this.write(batchPoints);
            else {
                this.influxDB.enableBatch(batchActions, batchFlushInterval, TimeUnit.SECONDS).write(batchPoints);
                this.isBatchEnabled=true;
            }
        } catch (Exception e) {
            String errCmd = String.format("write batch points: %s", batchPoints.toString());
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
    }

    /**
     * create database if doesnt already exists
     * @param name of the desired db
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public void createDatabase(final String name) {
        try {
            logger.info(String.format("EXECUTING: influxdb create db %s", name));
            this.influxDB.createDatabase(name);
        } catch (Exception e) {
            String errCmd = String.format("create db: %s", name);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }

    }

    /**
     * delete database
     * @param name of the db
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public void deleteDatabase(final String name) {
        try {
            logger.info(String.format("EXECUTING: influxdb delete db %s", name));
            this.influxDB.deleteDatabase(name);
        } catch (Exception e) {
            String errCmd = String.format("delete db: %s", name);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }

    }

    /**
     * all existing databases
     * @return list of existing databases in influx
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public List<String> describeDatabases() {
        List<String> response = null;
        try {
            logger.debug("EXECUTING: influxdb describeDatabases");
            response = this.influxDB.describeDatabases();
        } catch (Exception e) {
            String errCmd = "describe db";
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
        return response;
    }


    /**
     * creates primary db retention
     * @param retentionName
     * @param dbName
     * @param retentionDuration
     * @param replecation
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public void createDBRetention(String retentionName, String dbName, String retentionDuration, String replecation) {
        String queryCmd = String.format("CREATE RETENTION POLICY %s ON %s DURATION %s REPLICATION %s DEFAULT", retentionName, dbName, retentionDuration, replecation);
        logger.debug("EXECUTING: %s", queryCmd);
        Query retentionQuery = new Query(queryCmd, dbName);
        query(retentionQuery);
    }

    /**
     * check if influxdb is up and running
     * @return true if running, false otherwise
     */
    @Retryable(maxAttempts = INFLUX_MAX_ATTEMPTS, backoff = @Backoff(delay = INFLUX_DELAY_BETWEEN_ATTEMPTS))
    public boolean isInfluxDBStarted() {
        boolean influxDBstarted = false;
        do {
            Pong response;
            try {
                logger.debug("EXECUTING: influxdb connection test");
                Instant start = Instant.now();
                response = this.influxDB.ping();
                Instant end = Instant.now();
                logger.debug(String.format("FINISHED: finished executing connection test, duration: %s", Duration.between(start, end).toString()));
                if (!response.getVersion().equalsIgnoreCase("unknown")) {
                    influxDBstarted = true;
                }
            } catch (Exception e) {
                String errCmd = "failed to connect influxdb";
                if (e instanceof RetrofitError)
                    if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                        throw new InfluxDBNetworkExcpetion(errCmd, e);
                throw new InfluxDBRuntimeException(errCmd, e);
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new InfluxDBRuntimeException("connetion test InterruptedException", e);
            }
        } while (!influxDBstarted);

        return influxDBstarted;
    }
}
