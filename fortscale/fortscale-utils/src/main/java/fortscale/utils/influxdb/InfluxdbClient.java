package fortscale.utils.influxdb;

import fortscale.utils.influxdb.Exception.InfluxDBGeneralException;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        this.influxDB.setLogLevel(InfluxDB.LogLevel.valueOf(logLevel));
    }

    public QueryResult query(final Query query) throws InfluxDBGeneralException {
        QueryResult response = null;
        try {
            response = this.influxDB.query(query);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException(query.getCommand());
        }
        return response;
    }

    public QueryResult query(final Query query, TimeUnit timeUnit) throws InfluxDBGeneralException {
        QueryResult response = null;
        try {
            response = this.influxDB.query(query, timeUnit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException(query.getCommand());
        }
        return response;
    }

    public InfluxDB enableBatch(final int actions, final int flushDuration, final TimeUnit flushDurationTimeUnit) throws InfluxDBGeneralException {
        try {
            return this.influxDB.enableBatch(actions, flushDuration, flushDurationTimeUnit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException(String.format("enableBatch actions: %d, flushduration: %d TimeUnit: %s", actions, flushDuration, flushDurationTimeUnit.name()));
        }
    }

    public void disableBatch() throws InfluxDBGeneralException {
        try {
            this.influxDB.disableBatch();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException("disableBatch");
        }
    }

    public void write(final String database, final String retentionPolicy, final Point point) throws InfluxDBGeneralException {
        try {
            this.influxDB.write(database, retentionPolicy, point);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException(String.format("write db: %s, retention: %s, point: %s", database, retentionPolicy, point.toString()));
        }
    }

    public void write(final BatchPoints batchPoints) throws InfluxDBGeneralException {
        try {
            this.influxDB.write(batchPoints);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException(String.format("write batch points: %s", batchPoints.toString()));
        }
    }

    public void createDatabase(final String name) throws InfluxDBGeneralException {
        try {
            this.influxDB.createDatabase(name);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException(String.format("create db: %s", name));
        }
    }

    public void deleteDatabase(final String name) throws InfluxDBGeneralException {
        try {
            this.influxDB.deleteDatabase(name);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException(String.format("delete db: %s", name));
        }
    }

    public List<String> describeDatabases() throws InfluxDBGeneralException {
        List<String> response = null;
        try {
            response = this.influxDB.describeDatabases();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InfluxDBGeneralException("describe db");
        }
        return response;
    }

    public boolean isInfluxDBStarted() throws InfluxDBGeneralException {
        boolean influxDBstarted = false;
        do {
            Pong response;
            try {
                response = this.influxDB.ping();
                if (!response.getVersion().equalsIgnoreCase("unknown")) {
                    influxDBstarted = true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new InfluxDBGeneralException("ping db");
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                throw new InfluxDBGeneralException("ping db InterruptedException");
            }
        } while (!influxDBstarted);

        return influxDBstarted;
    }
}
