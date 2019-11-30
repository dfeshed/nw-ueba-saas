package com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen;

import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.EntityType;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

public class UncommonValuesEventsGen extends EventsGen {

    public UncommonValuesEventsGen(int dataPeriod, int uncommonStartDay, String name, String entity, EntityType entityType) {
        super(name, entity, entityType);
        super.daysBackFrom = dataPeriod;
        super.daysBackFromAnomaly = uncommonStartDay;
    }

    public UncommonValuesEventsGen setCommonValuesGen(final TlsRangeEventsGen gen) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getCommonValuesTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValuesEventsGen setCommonValuesGen(final TlsRangeEventsGen gen, int intervalMinutes) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getCommonValuesTimeGen(intervalMinutes));
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValuesEventsGen setUncommonValuesHistoryGen(final TlsRangeEventsGen gen) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesHistoryTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValuesEventsGen setUncommonValuesAnomalyGen(final TlsRangeEventsGen gen) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesAnomalyTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UncommonValuesEventsGen setUncommonValuesAnomalyGen(final TlsRangeEventsGen gen, int intervalMinutes) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getUncommonValuesAnomalyTimeGen(intervalMinutes));
        eventGenerators.add(copyGen);
        return this;
    }
}
