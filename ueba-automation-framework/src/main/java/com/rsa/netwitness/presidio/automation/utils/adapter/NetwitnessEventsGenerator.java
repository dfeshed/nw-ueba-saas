package com.rsa.netwitness.presidio.automation.utils.adapter;

import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public abstract class  NetwitnessEventsGenerator {
    public static final String HISTORICAL_DAYS_BACK_CONFIG_KEY = "historicalDaysBack";
    public static final String ANOMALY_DAY_CONFIG_KEY = "anomalyDay";
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(NetwitnessEventsGenerator.class.getName());
    protected Map<Schema, Long> eventGeneratorResult;

    public abstract void generateAndSave(Instant startDate, Instant endDate, Map<String, String> config);
    public abstract void save(List<Map<String, Object>> netwitnessEvents, Schema schema);


    public Map<Schema, Long> getEventGeneratorResult() {
        return eventGeneratorResult;
    }
}
