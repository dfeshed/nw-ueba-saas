package fortscale.utils.influxdb;

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

    private InfluxdbClient(String influxdbIP, String machinePort) {
        setInfluxdbIp(influxdbIP);
        setInfluxdbPort(machinePort);
        setInfluxdbUsername("admin");
        setInfluxdbPassword("");
        setInfluxDB(InfluxDBFactory.connect(String.format("http://%s:%s", getInfluxdbIp(), getInfluxdbPort()), getInfluxdbUsername(),getInfluxdbPassword()));
        setLogLevel(InfluxDB.LogLevel.BASIC);
    }

    public QueryResult query(final Query query) {
        QueryResult response = getInfluxDB().query(query);
        return response;
    }

    public QueryResult query(final Query query, TimeUnit timeUnit)
    {
        QueryResult response = getInfluxDB().query(query,timeUnit);
        return response;
    }
    public InfluxDB enableBatch(final int actions, final int flushDuration, final TimeUnit flushDurationTimeUnit)
    {
        return getInfluxDB().enableBatch(actions,flushDuration,flushDurationTimeUnit);
    }
    public void disableBatch()
    {
        getInfluxDB().disableBatch();
    }

    public void write(final String database, final String retentionPolicy, final Point point)
    {
        getInfluxDB().write(database,retentionPolicy,point);
    }

    public void write(final BatchPoints batchPoints)
    {
        getInfluxDB().write(batchPoints);
    }

    public void createDatabase(final String name)
    {
        getInfluxDB().createDatabase(name);
    }

    public void deleteDatabase(final String name)
    {
        getInfluxDB().deleteDatabase(name);
    }

    public List<String> describeDatabases()
    {
        return getInfluxDB().describeDatabases();
    }

    public InfluxDB setLogLevel(final InfluxDB.LogLevel logLevel)
    {
        return getInfluxDB().setLogLevel(logLevel);
    }

    public boolean isInfluxDBStarted()
    {
        boolean influxDBstarted = false;
        do {
            Pong response;
            try {
                response = getInfluxDB().ping();
                if (!response.getVersion().equalsIgnoreCase("unknown")) {
                    influxDBstarted = true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            }
        } while (!influxDBstarted);

        return influxDBstarted;
    }

    public InfluxDB getInfluxDB() {
        return influxDB;
    }

    private void setInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    private String getInfluxdbIp() {
        return influxdbIp;
    }

    private void setInfluxdbIp(String influxdbIp) {
        this.influxdbIp = influxdbIp;
    }

    private String getInfluxdbPort() {
        return influxdbPort;
    }

    private void setInfluxdbPort(String influxdbPort) {
        this.influxdbPort = influxdbPort;
    }

    private String getInfluxdbUsername() {
        return influxdbUsername;
    }

    private void setInfluxdbUsername(String influxdbUsername) {
        this.influxdbUsername = influxdbUsername;
    }

    private String getInfluxdbPassword() {
        return influxdbPassword;
    }

    private void setInfluxdbPassword(String influxdbPassword) {
        this.influxdbPassword = influxdbPassword;
    }
}
