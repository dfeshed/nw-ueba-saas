package com.rsa.netwitness.presidio.automation.data.preparation.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsIndicator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

public class HighNumberOfSrcIPs {

    private final int DISTINCT_IPS_AMOUNT = 15;
    private final int EVENTS_INTERVAL_MINUTES = 10;

    private final String entity;
    private final EntityType entityType;
    TlsIndicator indicator;
    UncommonValuesEventsGen eventsSupplier;

    public HighNumberOfSrcIPs(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }


    public TlsRangeEventsGen createHighNumberOfDistinctSrcIpGen(TlsRangeEventsGen initialGen){
        setEntity(initialGen);
        indicator.addNormalValues(initialGen.srcIpGenerator.getGenerator().getAllValues());
        indicator.addContext(initialGen.ja3Gen.getGenerator().getAllValues());
        eventsSupplier.setCommonValuesGen(initialGen);

        initialGen.srcIpGenerator.nextRangeGenCyclic(DISTINCT_IPS_AMOUNT);
        indicator.addNormalValues(initialGen.srcIpGenerator.getGenerator().getAllValues());
        eventsSupplier.setUncommonValuesAnomalyGen(initialGen, EVENTS_INTERVAL_MINUTES);
        return initialGen.copy();
    }


    public TlsRangeEventsGen createHighNumberOfDistinctSrcIpForNewEntityGen(TlsRangeEventsGen initialGen){
        setEntity(initialGen);

        indicator.addNormalValues(initialGen.srcIpGenerator.getGenerator().getAllValues());
        indicator.addContext(initialGen.sslSubjectGen.getGenerator().getAllValues());
        eventsSupplier.setCommonValuesGen(initialGen, 30);

        TlsRangeEventsGen abnormalGen = new TlsRangeEventsGen(1);
        abnormalGen.sslSubjectGen.setGenerator(initialGen.sslSubjectGen.getGenerator());
        abnormalGen.srcIpGenerator.nextRangeGenCyclic(DISTINCT_IPS_AMOUNT);
        eventsSupplier.setUncommonValuesAnomalyGen(abnormalGen, EVENTS_INTERVAL_MINUTES);
        return initialGen.copy();
    }


    public TlsIndicator getIndicator() {
        indicator.setEventsGenerator(eventsSupplier);
        return indicator;
    }
    
    private void setEntity(TlsRangeEventsGen gen) {
        if (entityType.equals(EntityType.JA3)) {
            gen.ja3Gen.setConstantValueGen(entity);
        } else if (entityType.equals(EntityType.SSL_SUBJECT)) {
            gen.sslSubjectGen.setConstantValueGen(entity);
        } else  {
            throw new EnumConstantNotPresentException(entityType.getClass(), "Setter is missing for entity type " + entityType.name());
        }
    }


}
