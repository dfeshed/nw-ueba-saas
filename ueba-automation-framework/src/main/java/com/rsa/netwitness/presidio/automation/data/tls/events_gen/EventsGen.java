package com.rsa.netwitness.presidio.automation.data.tls.events_gen;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.tls.feilds_gen.TlsEventsGen;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.SingleTimeGeneratorFactory;
import presidio.data.generators.event.network.NetworkEventsGenerator;

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
    private final String entityType;

    private Lazy<List<NetworkEvent>> eventsHolder = new Lazy<>();

    public EventsGen(String title, String entity, String entityType) {
        this.title = title;
        this.entity = entity;
        this.entityType = entityType;
    }

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
    private List<Integer> commonHours = IntStream.rangeClosed(startHourOfDay, endHourOfDay).boxed().collect(Collectors.toList());

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

    protected ITimeGenerator getAnomalyDayUnregularHoursTimeGen(int startHourOfDay, int endHourOfDay) {
        assertThat(startHourOfDay).isNotIn(commonHours);
        assertThat(endHourOfDay).isNotIn(commonHours);
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFromAnomaly, daysBackToAnomaly, intervalMinutesAnomaly);
    }

    protected ITimeGenerator getUnregularHoursHistoryTimeGen(int startHourOfDay, int endHourOfDay) {
        assertThat(startHourOfDay).isNotIn(commonHours);
        assertThat(endHourOfDay).isNotIn(commonHours);
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
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

    protected Function<NetworkEventsGenerator, List<NetworkEvent>> generate = gen -> {
        try {
            List<NetworkEvent> result = gen.generate();
            LOGGER.info(gen.getTimeGenerator().getFirst().toString() + " - "
                    + gen.getTimeGenerator().getLast().toString() + "; Count: " + result.size());
            return result;
        } catch (GeneratorException e) {
            e.printStackTrace();
            fail("Failed to generate events.");
        }
        return Lists.newLinkedList();
    };

    private List<NetworkEvent> generateAll() {
        LOGGER.info("Generating events for: " + entityType + " # " + entity + " # " + title);
        assertThat(eventGenerators).isNotEmpty();
        List<NetworkEvent> events = Lists.newLinkedList();
        eventGenerators.forEach(generator -> events.addAll(generate.apply(generator)));
        LOGGER.debug("Indicator events count: " + events.size());
        return events;
    }
}
