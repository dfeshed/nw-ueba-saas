package com.rsa.netwitness.presidio.automation.data.preparation.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsIndicator;
import presidio.data.generators.common.list.RangeGenerator;
import presidio.data.generators.event.tls.FieldRangeAllocator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.function.Function;

public class UncommonTime<U> {

    private final int UNCOMMON_DATA_VALUES = 2;

    private final String entity;
    TlsIndicator indicator;
    UncommonValuesEventsGen eventsSupplier;
    private final EntityType entityType;


    RangeGenerator<String> contextGen;
    RangeGenerator<U> commonGen;
    RangeGenerator<U> uncommonGen;

    public Function<U, String> valueToString = String::valueOf;


    public UncommonTime(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }

    public void setGenerators(TlsRangeEventsGen commonTimeHistoryGen, FieldRangeAllocator<String> entityGen, FieldRangeAllocator<String> valueGen){



    }


    public TlsIndicator getIndicator() {
        indicator.setEventsGenerator(eventsSupplier);
        return indicator;
    }

}
