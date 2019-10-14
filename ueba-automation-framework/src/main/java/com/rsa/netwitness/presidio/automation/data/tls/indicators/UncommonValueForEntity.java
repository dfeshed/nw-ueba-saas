package com.rsa.netwitness.presidio.automation.data.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsIndicator;
import presidio.data.generators.common.list.RangeGenerator;
import presidio.data.generators.event.tls.FieldRangeAllocator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.function.Function;

public class UncommonValueForEntity<U> {

    private final int UNCOMMON_DATA_VALUES = 2;

    private final String entity;
    TlsIndicator indicator;
    UncommonValuesEventsGen eventsSupplier;
    private final EntityType entityType;


    RangeGenerator<String> contextGen;
    RangeGenerator<U> commonGen;
    RangeGenerator<U> uncommonGen;

    public Function<U, String> valueToString = String::valueOf;


    public UncommonValueForEntity(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }

    public TlsRangeEventsGen createCommonValuesGen(TlsRangeEventsGen initialGen, FieldRangeAllocator<String> entityGen, FieldRangeAllocator<U> commonGen){
        this.contextGen = entityGen.setConstantValueGen(entity);
        this.commonGen = commonGen.getGenerator();
        eventsSupplier.setCommonValuesGen(initialGen);
        return initialGen.copy();
    }

    public TlsRangeEventsGen createUncommonValuesHistoryGen(TlsRangeEventsGen uncommonHistoryGen, FieldRangeAllocator<String> entityGen, FieldRangeAllocator<U> uncommonValuesGen){
        entityGen.nextRangeGenCyclic(UNCOMMON_DATA_VALUES);
        this.uncommonGen = uncommonValuesGen.nextRangeGenCyclic(UNCOMMON_DATA_VALUES);
        eventsSupplier.setUncommonValuesHistoryGen(uncommonHistoryGen);
        return uncommonHistoryGen.copy();
    }

    public TlsRangeEventsGen createUncommonValuesAnomalyGen(TlsRangeEventsGen uncommonEventsGen, FieldRangeAllocator<String> entityGen, FieldRangeAllocator<U> uncommonValuesGen){
        uncommonValuesGen.setGenerator(this.uncommonGen);
        eventsSupplier.setUncommonValuesAnomalyGen(uncommonEventsGen);
        return uncommonEventsGen.copy();
    }

    public TlsIndicator getIndicator() {
        indicator.addNormalValues(commonGen.getAllValuesToString(valueToString));
        indicator.addContext(contextGen.getAllValues());
        indicator.addAbnormalValues(uncommonGen.getAllValuesToString(valueToString));
        indicator.setEventsGenerator(eventsSupplier);

        return indicator;
    }

}
