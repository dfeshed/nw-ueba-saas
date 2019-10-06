package com.rsa.netwitness.presidio.automation.data.tls.events_gen;

import com.rsa.netwitness.presidio.automation.data.tls.feilds_gen.TlsEventsGen;

public class TlsIndicatorEventsGen extends EventsGen {

    public TlsIndicatorEventsGen(int dataPeriod, int uncommonStartDay) {
        daysBackFrom = dataPeriod;
        daysBackFromAnomaly = uncommonStartDay;
    }

    public TlsIndicatorEventsGen setCommonValuesGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getCommonValuesTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public TlsIndicatorEventsGen setUncommonValuesHistoryGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesHistoryTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public TlsIndicatorEventsGen setUncommonValuesAnomalyGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesAnomalyTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }
}
