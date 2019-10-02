package com.rsa.netwitness.presidio.automation.data.tls.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TlsAlert {
    public final String entity;
    public final String entityType;

    protected List<TlsIndicator> indicators = new LinkedList<>();
    protected Set<String> indicatorNames = new HashSet<>();


    protected TlsAlert(String entity, String entityType) {
        this.entity = entity;
        this.entityType = entityType;
    }

    public List<TlsIndicator> getIndicators() {
        return new LinkedList<>(indicators);
    }

    public TlsIndicator getIndicator(String name) {
        return indicators.stream().filter(e -> e.name.equals(name)).findFirst().orElse(null);
    }

    public Set<String> getIndicatorNames() {
        return new HashSet<>(indicatorNames);
    }



}
