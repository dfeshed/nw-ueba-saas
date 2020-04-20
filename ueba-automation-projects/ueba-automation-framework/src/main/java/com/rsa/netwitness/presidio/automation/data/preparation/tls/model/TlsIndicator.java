package com.rsa.netwitness.presidio.automation.data.preparation.tls.model;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen.EventsGen;
import org.slf4j.LoggerFactory;
import presidio.data.domain.event.network.TlsEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class TlsIndicator {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(TlsIndicator.class);

    public final String entity;
    public final EntityType entityType;
    public final String name;

    List<String> keys = new ArrayList<>();
    List<String> normalValues = new ArrayList<>();
    List<String> abnormalValues = new ArrayList<>();
    EventsGen eventsGenerator;
    Instant unregularHoursStartTime;

    public TlsIndicator(String entity, EntityType entityType, String name) {
        LOGGER.info("    ---> Entity: " + entity);
        this.name = name;
        this.entity = entity;
        this.entityType = entityType;
    }

    public List<String> getKeys() {
        return  new ArrayList<>(keys);
    }

    public List<String> getNormalValues() {
        return  new ArrayList<>(normalValues);
    }

    public List<String> getAbnormalValues() {
        return new ArrayList<>(abnormalValues);
    }

    public List<TlsEvent> generateEvents() {
        return eventsGenerator.getEvents();
    }

    public void setEventsGenerator(EventsGen eventsGenerator) {
        this.eventsGenerator = eventsGenerator;
    }

    public void addContext(List<String> values) {
        LOGGER.info("    ---> Context: " + String.join( ", ", values));
        keys.addAll(values);
    }
    public void addNormalValues(List<String> values) {
        LOGGER.info("    ---> Normal values: " + String.join( ", ", values));
        normalValues.addAll(values);
    }
    public void addAbnormalValues(List<String> values) {
        LOGGER.info("    ---> Abnormal values: " + String.join( ", ", values));
        abnormalValues.addAll(values);
    }

    public <T> void addContext(List<T> values, Function<T, String> toString) {
        List<String> valuesConverted = values.stream().map(toString).collect(toList());
        addContext(valuesConverted);
    }
    public <T> void addNormalValues(List<T> values, Function<T, String> toString) {
        List<String> valuesConverted = values.stream().map(toString).collect(toList());
        addNormalValues(valuesConverted);
    }
    public <T> void addAbnormalValues(List<T> values, Function<T, String> toString) {
        List<String> valuesConverted = values.stream().map(toString).collect(toList());
        addAbnormalValues(valuesConverted);
    }

}
