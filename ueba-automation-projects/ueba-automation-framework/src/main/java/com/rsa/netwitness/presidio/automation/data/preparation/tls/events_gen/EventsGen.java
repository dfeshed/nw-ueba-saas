package com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.SingleTimeGeneratorFactory;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

public abstract class EventsGen {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(EventsGen.class);
    public final String title;
    private final String entity;
    private final EntityType entityType;

    private Lazy<List<TlsEvent>> eventsHolder = new Lazy<>();

    EventsGen(String title, String entity, EntityType entityType) {
        this.title = title;
        this.entity = entity;
        this.entityType = entityType;
    }

    public  List<TlsEvent> getEvents() {
        return eventsHolder.getOrCompute(this::generateAll);
    }


    protected List<TlsRangeEventsGen> eventGenerators = Lists.newLinkedList();

    private int newOccurrencesOffset = 2;
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
    private List<Integer> commonHours = IntStream.rangeClosed(startHourOfDay, endHourOfDay).boxed().collect(Collectors.toList());




    protected ITimeGenerator getCommonValuesTimeGenForNewOccurrences(int intervalMinutes) {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom - newOccurrencesOffset, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getEntityHistoricalDataTimeGen() {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getUncommonValuesHistoryTimeGen() {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getCommonValuesTimeGen() {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getCommonValuesTimeGen(int intervalMinutes) {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getUncommonValuesAnomalyTimeGen() {
        return getTimeGen(startHourOfDayAnomaly, endHourOfDayAnomaly, daysBackFromAnomaly, daysBackToAnomaly, intervalMinutesAnomaly);
    }

    protected ITimeGenerator getUncommonValuesAnomalyTimeGen(int intervalMinutes) {
        return getTimeGen(startHourOfDayAnomaly, endHourOfDayAnomaly, daysBackFromAnomaly, daysBackToAnomaly, intervalMinutes);
    }

    protected ITimeGenerator getAnomalyDayUnregularHoursTimeGen(int startHourOfDay, int endHourOfDay) {
        assertThat(startHourOfDay).isNotIn(commonHours);
        assertThat(endHourOfDay).isNotIn(commonHours);
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFromAnomaly, daysBackToAnomaly, intervalMinutesAnomaly);
    }

    protected ITimeGenerator getUnregularHoursHistoryTimeGen(int startHourOfDay, int endHourOfDay) {
        assertThat(startHourOfDay).isNotIn(commonHours);
        assertThat(endHourOfDay).isNotIn(commonHours);
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, 15);
    }

    protected ITimeGenerator getTimeGen(int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo, int intervalMinutes) {
        try {
            return new SingleTimeGeneratorFactory(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes).createTimeGenerator();
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Function<TlsRangeEventsGen, List<TlsEvent>> generate = gen -> {
        try {
            gen.resetCounters();
            List<TlsEvent> result = gen.generate();
            LOGGER.info(gen.getTimeGenerator().getFirst().toString() + " - "
                    + gen.getTimeGenerator().getLast().toString() + "; Count: " + result.size());
            return result;
        } catch (GeneratorException e) {
            e.printStackTrace();
            fail("Failed to generate events.");
        }
        return Lists.newLinkedList();
    };

    private List<TlsEvent> generateAll() {
        LOGGER.info("Generating events for: " + entityType + " # " + entity + " # " + title);
        assertThat(eventGenerators).isNotEmpty();
        List<TlsEvent> events = Lists.newLinkedList();
        eventGenerators.forEach(generator -> events.addAll(generate.apply(generator)));
        LOGGER.debug("Indicator events count: " + events.size());
        return events;
    }
}
