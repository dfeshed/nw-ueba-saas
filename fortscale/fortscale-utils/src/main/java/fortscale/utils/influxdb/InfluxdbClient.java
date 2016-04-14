package fortscale.utils.influxdb;

import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfluxdbClient {
    private static Logger logger = LoggerFactory.getLogger(InfluxdbClient.class);
    private InfluxDB influxDB;
    private String influxdbIp;
    private String influxdbPort;
    private String influxdbUsername;
    private String influxdbPassword;

    private InfluxdbClient(String influxdbIP, String machinePort, String logLevel) {
        this.influxdbIp = influxdbIP;
        this.influxdbPort = machinePort;
        this.influxdbUsername = "admin";
        this.influxdbPassword = "";
        this.influxDB = InfluxDBFactory.connect(String.format("http://%s:%s", this.influxdbIp, this.influxdbPort), this.influxdbUsername, this.influxdbPassword);
        this.influxDB.setLogLevel(InfluxDB.LogLevel.valueOf(logLevel)); //influxdb restApi logLevl
    }

    public QueryResult query(final Query query) {
        QueryResult response = null;
        try {
            logger.debug(String.format("EXECUTING: influxdb query: %s",query.getCommand()));
            Instant start = Instant.now();
            response = this.influxDB.query(query);
            Instant end = Instant.now();
            logger.debug(String.format("FINISHED: finished executing query %s , duration: %s",query.getCommand(),Duration.between(start,end).toString()));
        } catch (Exception e) {
            throw new InfluxDBRuntimeException(query.getCommand(),e);
        }
        return response;
    }

    public QueryResult query(final Query query, TimeUnit timeUnit) {
        QueryResult response = null;
        try {
            logger.debug(String.format("EXECUTING: influxdb query: %s",query.getCommand()));
            Instant start = Instant.now();
            response = this.influxDB.query(query, timeUnit);
            Instant end = Instant.now();
            logger.debug(String.format("FINISHED: finished executing query %s , duration: %s",query.getCommand(),Duration.between(start,end).toString()));
        } catch (Exception e) {
            throw new InfluxDBRuntimeException(query.getCommand(),e);
        }
        return response;
    }

    public InfluxDB enableBatch(final int actions, final int flushDuration, final TimeUnit flushDurationTimeUnit) {
        try {
            logger.debug(String.format("EXECUTING: influxdb enableBatch actions: %d , flushDuration: %d, flushDurationTimeUnit: %s",actions,flushDuration,flushDurationTimeUnit.name()));
            return this.influxDB.enableBatch(actions, flushDuration, flushDurationTimeUnit);
        } catch (Exception e) {
            throw new InfluxDBRuntimeException(String.format("enableBatch actions: %d, flushduration: %d TimeUnit: %s", actions, flushDuration, flushDurationTimeUnit.name()),e);
        }
    }

    public void disableBatch() {
        try {
            logger.debug("EXECUTING: influxdb disableBatch");
            this.influxDB.disableBatch();
        } catch (Exception e) {
            throw new InfluxDBRuntimeException("disableBatch",e);
        }
    }

    public void write(final String database, final String retentionPolicy, final Point point) {
        try {
            logger.debug(String.format("EXECUTING: influxdb write: database: %s, retention: %s, point: %s",database,retentionPolicy,point.toString()));
            this.influxDB.write(database, retentionPolicy, point);
        } catch (Exception e) {
            throw new InfluxDBRuntimeException(String.format("write db: %s, retention: %s, point: %s", database, retentionPolicy, point.toString()),e);
        }
    }

    public void write(final BatchPoints batchPoints) {
        try {
            logger.debug(String.format("EXECUTING: influxdb batch write: %s",batchPoints.toString()));
            this.influxDB.write(batchPoints);
        } catch (Exception e) {
            throw new InfluxDBRuntimeException(String.format("write batch points: %s", batchPoints.toString()),e);
        }
    }

    public void createDatabase(final String name) {
        try {
            logger.info(String.format("EXECUTING: influxdb create db %s",name));
            this.influxDB.createDatabase(name);
        } catch (Exception e) {
            throw new InfluxDBRuntimeException(String.format("create db: %s", name),e);
        }
    }

    public void deleteDatabase(final String name) {
        try {
            logger.info(String.format("EXECUTING: influxdb delete db %s",name));
            this.influxDB.deleteDatabase(name);
        } catch (Exception e) {
            throw new InfluxDBRuntimeException(String.format("delete db: %s", name),e);
        }
    }

    public List<String> describeDatabases() {
        List<String> response = null;
        try {
            logger.debug("EXECUTING: influxdb describeDatabases");
            response = this.influxDB.describeDatabases();
        } catch (Exception e) {
            throw new InfluxDBRuntimeException("describe db",e);
        }
        return response;
    }

    public void createRetention(String retentionName, String dbName, String retentionDuration, String replecation)
    {
        String queryCmd= String.format("CREATE RETENTION POLICY %s ON %s DURATION %s REPLICATION %s DEFAULT",retentionName,dbName,retentionDuration,replecation);
        logger.debug("EXECUTING: %s",queryCmd);
        Query retentionQuery = new Query(queryCmd,dbName);
        query(retentionQuery);
    }

    public boolean isInfluxDBStarted() {
        boolean influxDBstarted = false;
        do {
            Pong response;
            try {
                logger.debug("EXECUTING: influxdb connection test");
                Instant start = Instant.now();
                response = this.influxDB.ping();
                Instant end = Instant.now();
                logger.debug(String.format("FINISHED: finished executing connection test, duration: %s",Duration.between(start,end).toString()));
                if (!response.getVersion().equalsIgnoreCase("unknown")) {
                    influxDBstarted = true;
                }
            } catch (Exception e) {
                throw new InfluxDBRuntimeException("connection test",e);
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new InfluxDBRuntimeException("connetion test InterruptedException",e);
            }
        } while (!influxDBstarted);

        return influxDBstarted;
    }
}
