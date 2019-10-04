package com.rsa.netwitness.presidio.automation.data.tls.events;

import com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGen;

public class UncommonValuesEventsSupplier extends IndicatorGen {

    public UncommonValuesEventsSupplier(int dataPeriod, int uncommonStartDay) {
        daysBackFrom = dataPeriod;
        daysBackFromAnomaly = uncommonStartDay;
    }

    public UncommonValuesEventsSupplier setCommonValuesGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getCommonValuesTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValuesEventsSupplier setUncommonValuesHistoryGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesHistoryTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValuesEventsSupplier setUncommonValuesAnomalyGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesAnomalyTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }
}
