package com.rsa.netwitness.presidio.automation.data.tls.events_gen;

import com.rsa.netwitness.presidio.automation.data.tls.model.EntityType;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.time.Instant;

public class UnregularHoursIndicatorEventsGen extends EventsGen {
    private ITimeGenerator ja3UnregularHoursHistoryTimeGen = getUnregularHoursHistoryTimeGen(1,4);
    private ITimeGenerator ja3UnregularHoursAnomalyDayTimeGen = getAnomalyDayUnregularHoursTimeGen(1,4);

    private ITimeGenerator sslSubjectUnregularHoursHistoryTimeGen = getUnregularHoursHistoryTimeGen(4,7);
    private ITimeGenerator sslSubjectUnregularHoursAnomalyDayTimeGen = getAnomalyDayUnregularHoursTimeGen(4,7);

    public UnregularHoursIndicatorEventsGen(int dataPeriod, int uncommonStartDay, String name, String entity, EntityType entityType) {
        super(name, entity, entityType);
        daysBackFrom = dataPeriod;
        daysBackFromAnomaly = uncommonStartDay;
    }

    public UnregularHoursIndicatorEventsGen setRegularHoursHistoryGen(final TlsRangeEventsGen gen) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getCommonValuesTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UnregularHoursIndicatorEventsGen setUnregularHoursHistoryGenJa3(TlsRangeEventsGen gen) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(ja3UnregularHoursHistoryTimeGen);
        eventGenerators.add(copyGen);
        return this;
    }

    public UnregularHoursIndicatorEventsGen setAnomalyDayUnregularHoursGenJa3(final TlsRangeEventsGen gen) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(ja3UnregularHoursAnomalyDayTimeGen);
        eventGenerators.add(copyGen);
        return this;
    }


    public UnregularHoursIndicatorEventsGen setUnregularHoursHistoryGenSslSubject(TlsRangeEventsGen gen) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(sslSubjectUnregularHoursHistoryTimeGen);
        eventGenerators.add(copyGen);
        return this;
    }

    public UnregularHoursIndicatorEventsGen setAnomalyDayUnregularHoursGenSslSubject(final TlsRangeEventsGen gen) {
        TlsRangeEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(sslSubjectUnregularHoursAnomalyDayTimeGen);
        eventGenerators.add(copyGen);
        return this;
    }



    public Instant getUnregularStartTimeJa3() {
        try {
            return ja3UnregularHoursAnomalyDayTimeGen.getFirst();
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Instant getUnregularStartTimeSslSubject() {
        try {
            return sslSubjectUnregularHoursAnomalyDayTimeGen.getFirst();
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
        return null;
    }


}
