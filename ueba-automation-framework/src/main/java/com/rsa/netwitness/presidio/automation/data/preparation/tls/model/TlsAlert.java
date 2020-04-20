package com.rsa.netwitness.presidio.automation.data.preparation.tls.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TlsAlert {
    public final String entity;
    public final EntityType entityType;

    List<TlsIndicator> indicators = new LinkedList<>();
    Set<String> indicatorNames = new HashSet<>();


    TlsAlert(String entity, EntityType entityType) {
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
