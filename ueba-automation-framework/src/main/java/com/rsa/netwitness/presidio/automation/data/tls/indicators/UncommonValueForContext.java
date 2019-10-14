package com.rsa.netwitness.presidio.automation.data.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsIndicator;
import presidio.data.generators.common.list.RangeGenerator;
import presidio.data.generators.event.tls.FieldRangeAllocator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.function.Function;

public class UncommonValueForContext<T, U> {

    private final int UNCOMMON_DATA_VALUES = 2;

    private final String entity;
    private final EntityType entityType;
    TlsIndicator indicator;
    UncommonValuesEventsGen eventsSupplier;

    RangeGenerator<T> contextGen;
    RangeGenerator<U> commonGen;
    RangeGenerator<U> uncommonGen;

    public Function<U, String> valueToString = String::valueOf;
    public Function<T, String> contextToString = String::valueOf;


    public UncommonValueForContext(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }

    public TlsRangeEventsGen createCommonValuesGen(TlsRangeEventsGen initialGen, FieldRangeAllocator<T> contextGen, FieldRangeAllocator<U> commonGen){
        if (entityType.equals(EntityType.JA3)) {
            initialGen.ja3Gen.setConstantValueGen(entity);
        } else if (entityType.equals(EntityType.SSL_SUBJECT)) {
            initialGen.sslSubjectGen.setConstantValueGen(entity);
        } else  {
            throw new EnumConstantNotPresentException(entityType.getClass(), "Setter is missing for entity type " + entityType.name());
        }

        this.contextGen = contextGen.getGenerator();
        this.commonGen = commonGen.getGenerator();

        eventsSupplier.setCommonValuesGen(initialGen);
        return initialGen.copy();
    }

    public TlsRangeEventsGen createUncommonValuesHistoryGen(TlsRangeEventsGen uncommonHistoryGen, FieldRangeAllocator<T> contextGen, FieldRangeAllocator<U> uncommonValuesGen){
        contextGen.nextRangeGenCyclic(UNCOMMON_DATA_VALUES);
        this.uncommonGen =  uncommonValuesGen.nextRangeGenCyclic(UNCOMMON_DATA_VALUES);
        eventsSupplier.setUncommonValuesHistoryGen(uncommonHistoryGen);
        return uncommonHistoryGen.copy();
    }

    public TlsRangeEventsGen createUncommonValuesAnomalyGen(TlsRangeEventsGen uncommonEventsGen, FieldRangeAllocator<T> contextGen, FieldRangeAllocator<U> uncommonValuesGen){
        contextGen.setGenerator(this.contextGen);
        uncommonValuesGen.setGenerator(this.uncommonGen);
        eventsSupplier.setUncommonValuesAnomalyGen(uncommonEventsGen);

        return uncommonEventsGen.copy();
    }

    public TlsIndicator getIndicator() {
        indicator.addNormalValues(commonGen.getAllValuesToString(valueToString));
        indicator.addContext(contextGen.getAllValuesToString(contextToString));
        indicator.addAbnormalValues(uncommonGen.getAllValuesToString(valueToString));
        indicator.setEventsGenerator(eventsSupplier);

        return indicator;
    }




}
