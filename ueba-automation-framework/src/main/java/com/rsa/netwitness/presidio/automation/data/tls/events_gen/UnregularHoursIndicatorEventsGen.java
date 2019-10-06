package com.rsa.netwitness.presidio.automation.data.tls.events_gen;

import com.rsa.netwitness.presidio.automation.data.tls.feilds_gen.TlsEventsGen;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;

import java.time.Instant;

public class UnregularHoursIndicatorEventsGen extends EventsGen {
    private ITimeGenerator ja3UnregularHoursHistoryTimeGen = getUnregularHoursHistoryTimeGen(1,4);
    private ITimeGenerator ja3UnregularHoursAnomalyDayTimeGen = getAnomalyDayUnregularHoursTimeGen(1,4);

    private ITimeGenerator sslSubjectUnregularHoursHistoryTimeGen = getUnregularHoursHistoryTimeGen(4,7);
    private ITimeGenerator sslSubjectUnregularHoursAnomalyDayTimeGen = getAnomalyDayUnregularHoursTimeGen(4,7);

    public UnregularHoursIndicatorEventsGen(int dataPeriod, int uncommonStartDay, String name, String entity, String entityType) {
        super(name, entity, entityType);
        daysBackFrom = dataPeriod;
        daysBackFromAnomaly = uncommonStartDay;
    }

    public UnregularHoursIndicatorEventsGen setRegularHoursHistoryGen(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(getCommonValuesTimeGen());
        eventGenerators.add(copyGen);
        return this;
    }

    public UnregularHoursIndicatorEventsGen setUnregularHoursHistoryGenJa3(TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(ja3UnregularHoursHistoryTimeGen);
        eventGenerators.add(copyGen);
        return this;
    }

    public UnregularHoursIndicatorEventsGen setAnomalyDayUnregularHoursGenJa3(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(ja3UnregularHoursAnomalyDayTimeGen);
        eventGenerators.add(copyGen);
        return this;
    }


    public UnregularHoursIndicatorEventsGen setUnregularHoursHistoryGenSslSubject(TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
        copyGen.setTimeGenerator(sslSubjectUnregularHoursHistoryTimeGen);
        eventGenerators.add(copyGen);
        return this;
    }

    public UnregularHoursIndicatorEventsGen setAnomalyDayUnregularHoursGenSslSubject(final TlsEventsGen gen) {
        TlsEventsGen copyGen = gen.copy();
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
