package fortscale.utils.influxdb.impl;

import fortscale.utils.influxdb.Exception.InfluxDBNetworkExcpetion;
import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import fortscale.utils.influxdb.InfluxdbService;
import fortscale.utils.influxdb.metrics.InfluxdbMetrics;
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
public class InfluxdbServiceImpl implements InfluxdbService {
    private static Logger logger = Logger.getLogger(InfluxdbServiceImpl.class);
    private InfluxDB influxDB;
    private String influxdbIp;
    private String influxdbPort;
    private String influxdbUsername;
    private String influxdbPassword;
    private long readTimeout;
    private long writeTimeout;
    private long connectionTimout;
    private boolean isBatchEnabled;
    private int batchActions;
    private int batchFlushInterval;
    private InfluxdbMetrics influxdbMetrics;

    /**
     * InfluxdbService C'tor
     *
     * @param influxdbIP         influxdb ip
     * @param influxdbPort       influxdb port
     * @param apiLogLevel        rest api log level possible (available options are: NONE,BASIC,HEADERS,FULL)
     * @param readTimeout        timeout for read queries
     * @param writeTimeout       timeout for write operations
     * @param connectTimeout     timeout for connect
     * @param batchActions       number of points written in one batch.
     * @param batchFlushInterval batch flush interval in seconds
     * @param user               influxdb username
     * @param password           influxdb password
     */
    public InfluxdbServiceImpl(String influxdbIP, String influxdbPort, String apiLogLevel, long readTimeout, long writeTimeout, long connectTimeout, int batchActions, int batchFlushInterval, String user, String password, InfluxdbMetrics influxdbMetrics) {
        this.isBatchEnabled = false;
        this.influxdbIp = influxdbIP;
        this.influxdbPort = influxdbPort;
        this.influxdbUsername = user;
        this.influxdbPassword = password;
        this.influxDB = InfluxDBFactory.connect(String.format("http://%s:%s", this.influxdbIp, this.influxdbPort), this.influxdbUsername, this.influxdbPassword);
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.connectionTimout = connectTimeout;
        this.influxDB.setReadTimeout(readTimeout, TimeUnit.SECONDS);
        this.influxDB.setWriteTimeout(writeTimeout, TimeUnit.SECONDS);
        this.influxDB.setConnectTimeout(connectTimeout, TimeUnit.SECONDS);
        this.influxDB.setLogLevel(InfluxDB.LogLevel.valueOf(apiLogLevel)); //influxdb restApi logLevl
        this.batchActions = batchActions;
        this.batchFlushInterval = batchFlushInterval;
        this.influxdbMetrics = influxdbMetrics;
        logger.info("influxClient instance got created ip: {} port: {} user: {} readTimeout: {} writeTimeout: {} connectTimeout: {}", influxdbIP, influxdbPort, user, readTimeout, writeTimeout, connectTimeout);
    }

    /**
     * query influxdb
     *
     * @param query - containing db name and command
     * @return query result
     */
    public QueryResult query(final Query query) {
        QueryResult response;
        try {
            influxdbMetrics.queries++;
            logger.debug("EXECUTING: influxdb query: {}", query.getCommand());
            response = this.influxDB.query(query);
            logger.debug("FINISHED: finished executing query {} ", query.getCommand());
            influxdbMetrics.pointsRead += response.getResults().size();
        } catch (Exception e) {
            influxdbMetrics.queryFailures++;
            String errCmd = String.format("query: %s ip: %s port: %s readTimeout: %d", query.getCommand(), this.influxdbIp, this.influxdbPort, this.readTimeout);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
        return response;
    }

    /**
     * query influxdb
     *
     * @param query    - containing db name and command
     * @param timeUnit - of the result
     * @return query
     */
    public QueryResult query(final Query query, TimeUnit timeUnit) {
        QueryResult response;
        try {
            influxdbMetrics.queries++;
            logger.debug("EXECUTING: influxdb query: {}", query.getCommand());
            response = this.influxDB.query(query, timeUnit);
            logger.debug("FINISHED: finished executing query {}", query.getCommand());
            influxdbMetrics.pointsRead += response.getResults().size();
        } catch (Exception e) {
            influxdbMetrics.queryFailures++;
            String errCmd = String.format("query: %s ip: %s, port: %s, readTimeout: %d", query.getCommand(), this.influxdbIp, this.influxdbPort, this.readTimeout);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
        return response;
    }

    /**
     * enable batch writes to influx
     *
     * @param actions               number of actions
     * @param flushDuration         number of TIMEUNIT (i.e. 10 seconds) before flush
     * @param flushDurationTimeUnit time unit of flush duration
     */
    public void enableBatch(final int actions, final int flushDuration, final TimeUnit flushDurationTimeUnit) {
        try {
            logger.debug("EXECUTING: influxdb enableBatch actions: {} , flushDuration: {}, flushDurationTimeUnit: {}", actions, flushDuration, flushDurationTimeUnit.name());
            this.influxDB.enableBatch(actions, flushDuration, flushDurationTimeUnit);
            isBatchEnabled = true;
        } catch (Exception e) {
            String errCmd = String.format("enableBatch actions: %d, flushduration: %d TimeUnit: %s", actions, flushDuration, flushDurationTimeUnit.name());
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
    }

    /**
     * disable batch
     */
    public void disableBatch() {
        try {
            logger.debug("EXECUTING: influxdb disableBatch");
            this.influxDB.disableBatch();
            isBatchEnabled = false;
        } catch (Exception e) {
            String errCmd = "disableBatch";
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
    }

    /**
     * writhing a point to influxdb
     *
     * @param database        db name
     * @param retentionPolicy
     * @param point
     */
    public void write(final String database, final String retentionPolicy, final Point point) {
        try {
            influxdbMetrics.writes++;
            logger.debug("EXECUTING: influxdb write: database: {}, retention: {}, point: {}", database, retentionPolicy, point.toString());
            this.influxDB.write(database, retentionPolicy, point);
        } catch (Exception e) {
            influxdbMetrics.writeFailures++;
            String errCmd = String.format("write db: %s, retention: %s, point: %s, ip: %s, port: %s, writeTimeout:%d", database, retentionPolicy, point.toString(), this.influxdbIp, this.influxdbPort, this.writeTimeout);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
    }

    /**
     * write batch points to db. enables batch point writes if previously disables.
     *
     * @param batchPoints
     */
    public void batchWrite(final BatchPoints batchPoints) {
        try {
            influxdbMetrics.batchwrites++;
            if (logger.isDebugEnabled()) {
                logger.debug("EXECUTING: influxdb batch write for {} objects: \n {}", batchPoints.getPoints().size(), batchPoints.toString());
            }
            if (this.isBatchEnabled)
                this.influxDB.write(batchPoints);
            else {
                this.enableBatch(batchActions, batchFlushInterval, TimeUnit.SECONDS);
                this.influxDB.write(batchPoints);
            }
            influxdbMetrics.pointsWritten += batchPoints.getPoints().size();
        } catch (Exception e) {
            influxdbMetrics.batchWriteFailures++;
            String errCmd = String.format("write batch points: %s, ip: %s, port: %s", batchPoints.toString(), this.influxdbIp, this.influxdbPort);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
    }

    /**
     * create database if doesnt already exists
     *
     * @param name of the desired db
     */
    public void createDatabase(final String name) {
        try {
            influxdbMetrics.createDb++;
            logger.info("EXECUTING: influxdb create db {}", name);
            this.influxDB.createDatabase(name);
        } catch (Exception e) {
            String errCmd = String.format("create db: %s ip: %s port: %s", name, this.influxdbIp, this.influxdbPort);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }

    }

    /**
     * delete database
     *
     * @param name of the db
     */
    public void deleteDatabase(final String name) {
        try {
            logger.info("EXECUTING: influxdb delete db {}", name);
            this.influxDB.deleteDatabase(name);
        } catch (Exception e) {
            String errCmd = String.format("delete db: %s, at ip: %s port: %s", name, this.influxdbIp, this.influxdbPort);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }

    }

    /**
     * get all existing databases name on influx
     *
     * @return list of existing databases in influx
     */
    public List<String> describeDatabases() {
        List<String> response;
        try {
            logger.debug("EXECUTING: influxdb describeDatabases");
            response = this.influxDB.describeDatabases();
            logger.debug("Influxdb describeDatabases response: {}", response.toString());
        } catch (Exception e) {
            String errCmd = String.format("describe db for at: %s port: %s", this.influxdbIp, this.influxdbPort);
            if (e instanceof RetrofitError)
                if (((RetrofitError) e).getKind().equals(RetrofitError.Kind.NETWORK))
                    throw new InfluxDBNetworkExcpetion(errCmd, e);
            throw new InfluxDBRuntimeException(errCmd, e);
        }
        return response;
    }


    /**
     * creates primary db retention
     *
     * @param retentionName     retention name
     * @param dbName            database name
     * @param retentionDuration ratation duration. for example 8w = 8 weeks
     * @param replication       replication for cluster support
     */
    public void createDBRetention(String retentionName, String dbName, String retentionDuration, String replication) {
        String queryCmd = String.format("CREATE RETENTION POLICY %s ON %s DURATION %s REPLICATION %s DEFAULT", retentionName, dbName, retentionDuration, replication);
        Query retentionQuery = new Query(queryCmd, dbName);
        query(retentionQuery);
    }

    /**
     * check if influxdb is up and running
     *
     * @return true if running, false otherwise
     */
    public boolean isInfluxDBStarted() {
        boolean influxDBstarted = false;
        do {
            Pong response;
            try {
                logger.debug("EXECUTING: influxdb connection test");
                response = this.influxDB.ping();
                logger.debug("FINISHED: finished executing connection test");
                if (!response.getVersion().equalsIgnoreCase("unknown")) {
                    influxDBstarted = true;
                }
            } catch (Exception e) {
                String errCmd = String.format("failed to connect influxdb ip: %s port: %s, connectionTimeout: %d", this.influxdbIp, this.influxdbPort, this.connectionTimout);
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
