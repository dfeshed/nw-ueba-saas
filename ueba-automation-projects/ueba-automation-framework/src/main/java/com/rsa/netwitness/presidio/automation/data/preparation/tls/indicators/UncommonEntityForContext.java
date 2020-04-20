package com.rsa.netwitness.presidio.automation.data.preparation.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsIndicator;
import presidio.data.generators.common.list.RangeGenerator;
import presidio.data.generators.event.tls.FieldRangeAllocator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.function.Function;

public class UncommonEntityForContext<T> {

    private final int UNCOMMON_DATA_VALUES = 2;

    private final String entity;
    TlsIndicator indicator;
    UncommonValuesEventsGen eventsSupplier;
    private final EntityType entityType;

    RangeGenerator<T> contextGen;
    RangeGenerator<String> commonEntityGen;
    RangeGenerator<String> uncommonEntityGen;

    Function<T, String> contextToString = String::valueOf;


    public UncommonEntityForContext(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }

    public TlsRangeEventsGen createCommonValuesGen(TlsRangeEventsGen initialGen, FieldRangeAllocator<T> contextGen, FieldRangeAllocator<String> entityGen){
        this.contextGen = contextGen.getGenerator();
        this.commonEntityGen = entityGen.getGenerator();

        eventsSupplier.setCommonValuesGen(initialGen);
        return initialGen.copy();
    }

    public TlsRangeEventsGen createUncommonValuesHistoryGen(TlsRangeEventsGen uncommonHistoryGen, FieldRangeAllocator<T> contextGen, FieldRangeAllocator<String> entityGen){
        contextGen.nextRangeGenCyclic(UNCOMMON_DATA_VALUES);
        this.uncommonEntityGen = entityGen.setConstantValueGen(entity);
        eventsSupplier.setUncommonValuesHistoryGen(uncommonHistoryGen);
        return uncommonHistoryGen.copy();
    }

    public TlsRangeEventsGen createUncommonValuesAnomalyGen(TlsRangeEventsGen uncommonEventsGen, FieldRangeAllocator<T> contextGen, FieldRangeAllocator<String> entityGen){
        contextGen.setGenerator(this.contextGen);
        entityGen.setGenerator(this.uncommonEntityGen);
        eventsSupplier.setUncommonValuesAnomalyGen(uncommonEventsGen);
        return uncommonEventsGen.copy();
    }

    public TlsIndicator getIndicator() {
        indicator.addNormalValues(commonEntityGen.getAllValues());
        indicator.addContext(contextGen.getAllValuesToString(contextToString));
        indicator.addAbnormalValues(uncommonEntityGen.getAllValues());
        indicator.setEventsGenerator(eventsSupplier);

        return indicator;
    }

}
