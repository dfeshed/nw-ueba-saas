package com.rsa.netwitness.presidio.automation.data.tls.model;

import com.rsa.netwitness.presidio.automation.data.tls.events.IndicatorGen;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TlsIndicator {
    public final String entity;
    public final String entityType;
    public final String name;

    List<String> keys = new ArrayList<>();
    List<String> normalValues = new ArrayList<>();
    List<String> abnormalValues = new ArrayList<>();
    IndicatorGen eventsGenerator;

    TlsIndicator(String entity, String entityType, String name) {
        this.name = name;
        this.entity = entity;
        this.entityType = entityType;
    }

    public List<String> getNormalValues() {
        return  new ArrayList<>(normalValues);
    }

    public List<String> getAbnormalValues() {
        return new ArrayList<>(abnormalValues);
    }

    public List<NetworkEvent> getEvents() {
        return eventsGenerator.getEvents();
    }

    public void setEventsGenerator(IndicatorGen eventsGenerator) {
        this.eventsGenerator = eventsGenerator;
    }

    public void addKeys(List<String> values) {
        keys.addAll(values);
    }

    public void addNormalValues(List<String> values) {
        normalValues.addAll(values);
    }

    public void addNormalValuesNum(List<Number> values) {
        normalValues.addAll(values.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    public void addAbnormalValues(List<String> values) {
        abnormalValues.addAll(values);
    }

    public void addAbnormalValuesNum(List<Number> values) {
        abnormalValues.addAll(values.stream().map(String::valueOf).collect(Collectors.toList()));
    }
}
