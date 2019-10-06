package com.rsa.netwitness.presidio.automation.data.tls.events_gen;

import com.rsa.netwitness.presidio.automation.data.tls.feilds_gen.TlsEventsGen;

public class UncommonValueIndicatorEventsGen extends EventsGen {

    public UncommonValueIndicatorEventsGen(int dataPeriod, int uncommonStartDay, String name, String entity, String entityType) {
        super(name, entity, entityType);
        daysBackFrom = dataPeriod;
        daysBackFromAnomaly = uncommonStartDay;
    }

    public UncommonValueIndicatorEventsGen setCommonValuesGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getCommonValuesTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValueIndicatorEventsGen setUncommonValuesHistoryGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesHistoryTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValueIndicatorEventsGen setUncommonValuesAnomalyGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesAnomalyTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }
}
