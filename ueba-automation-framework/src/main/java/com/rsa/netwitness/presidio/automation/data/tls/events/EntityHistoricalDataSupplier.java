package com.rsa.netwitness.presidio.automation.data.tls.events;

import com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGen;

public class EntityHistoricalDataSupplier extends IndicatorGen {

    public EntityHistoricalDataSupplier(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getEntityHistoricalDataTimeGen());
        eventGenerators.add(copyGen);
    }
}
