package com.rsa.netwitness.presidio.automation.data.tls.events;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGen;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.SingleTimeGeneratorFactory;
import presidio.data.generators.event.network.NetworkEventsGenerator;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

public abstract class IndicatorGen {

    private Lazy<List<NetworkEvent>> eventsHolder = new Lazy<>();

    public  List<NetworkEvent> getEvents() {
        return eventsHolder.getOrCompute(this::generateAll);
    }


    protected List<TlsEventsGen> eventGenerators = Lists.newLinkedList();

    protected int startHourOfDay = 8;
    protected int endHourOfDay = 17;
    protected int daysBackFrom = 30;
    protected int daysBackTo = 1;   // -1 is current day
    protected int startHourOfDayAnomaly = 8;
    protected int endHourOfDayAnomaly = 17;
    protected int daysBackFromAnomaly = 1;
    protected int daysBackToAnomaly = 0;
    protected int intervalMinutes = 60;
    protected int intervalMinutesAnomaly = 60;

    protected ITimeGenerator getEntityHistoricalDataTimeGen() {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getUncommonValuesHistoryTimeGen() {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getCommonValuesTimeGen() {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getUncommonValuesAnomalyTimeGen() {
        return getTimeGen(startHourOfDayAnomaly, endHourOfDayAnomaly, daysBackFromAnomaly, daysBackToAnomaly, intervalMinutesAnomaly);
    }

    protected ITimeGenerator getDefaultUnregularHoursTimeGen() {
        return getTimeGen(2, 5, daysBackFromAnomaly, daysBackToAnomaly, intervalMinutesAnomaly);
    }

    protected ITimeGenerator getTimeGen(int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo, int intervalMinutes) {
        try {
            return new SingleTimeGeneratorFactory(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes)
                    .createTimeGenerator();
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<NetworkEvent> generateAll() {
        assertThat(eventGenerators).isNotEmpty();
        List<NetworkEvent> events = Lists.newLinkedList();
        eventGenerators.forEach(generator -> events.addAll(generate.apply(generator)));
        return events;
    }

    protected Function<NetworkEventsGenerator, List<NetworkEvent>> generate = gen -> {
        try {
            return gen.generate();
        } catch (GeneratorException e) {
            e.printStackTrace();
            fail("Failed to generate events.");
        }
        return Lists.newLinkedList();
    };
}
