package com.rsa.netwitness.presidio.automation.data.tls.model;

import com.rsa.netwitness.presidio.automation.data.tls.events_gen.EventsGen;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class TlsIndicator {
    public final String entity;
    public final String entityType;
    public final String name;

    List<String> keys = new ArrayList<>();
    List<String> normalValues = new ArrayList<>();
    List<String> abnormalValues = new ArrayList<>();
    EventsGen eventsGenerator;

    TlsIndicator(String entity, String entityType, String name) {
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

    public List<NetworkEvent> generateEvents() {
        return eventsGenerator.getEvents();
    }

    void setEventsGenerator(EventsGen eventsGenerator) {
        this.eventsGenerator = eventsGenerator;
    }

    void addKeys(List<String> values) {
        keys.addAll(values);
    }
    void addNormalValues(List<String> values) {
        normalValues.addAll(values);
    }
    void addAbnormalValues(List<String> values) {
        abnormalValues.addAll(values);
    }

    <T> void addKeys(List<T> values, Function<T, String> toString) {
        keys.addAll(values.stream().map(toString).collect(toList()));
    }
    <T> void addNormalValues(List<T> values, Function<T, String> toString) {
        normalValues.addAll(values.stream().map(toString).collect(toList()));
    }
    <T> void addAbnormalValues(List<T> values, Function<T, String> toString) {
        normalValues.addAll(values.stream().map(toString).collect(toList()));
    }

}
