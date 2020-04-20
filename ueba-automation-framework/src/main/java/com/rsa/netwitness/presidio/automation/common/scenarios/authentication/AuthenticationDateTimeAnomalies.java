package com.rsa.netwitness.presidio.automation.common.scenarios.authentication;

import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.List;

public class AuthenticationDateTimeAnomalies {

    public static List<AuthenticationEvent> getAbnormalLunchTimeActivity(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        // most of events are during "normal" work hours, except lunch time
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(11, 59), 30, anomalyDay + 23, anomalyDay + 8);
        List<AuthenticationEvent> events = prepareUserTimedEvents(testUser, eventIdGen, timeGenerator1);

        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 00), LocalTime.of(18, 00), 30, anomalyDay + 23, anomalyDay + 8);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator2));

        // abnormal activity during "lunch time" - for last day
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(12, 40), LocalTime.of(13, 00), 1, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator3));

        return events;
    }

    public static List<AuthenticationEvent> getNormalTimeActivity(String testUser) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        // most of events are during "normal" work hours, except lunch time
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(16, 30), 30, 35, 0);
        List<AuthenticationEvent> events = prepareUserTimedEvents(testUser, eventIdGen, timeGenerator1);
        return events;
    }

    public static List<AuthenticationEvent> getAnomalyOnTwoNormalIntervalsActivity(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        // "normal" work hours are in 2 intervals - morning/evening
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(11, 59), 30, anomalyDay + 28, anomalyDay + 13);
        List<AuthenticationEvent> events = prepareUserTimedEvents(testUser, eventIdGen, timeGenerator1);

        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 00), LocalTime.of(21, 00), 30, anomalyDay + 28, anomalyDay + 13);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator2));

        // abnormal activity at midday/midnight
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 30), LocalTime.of(14, 30), 10, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator3));

        ITimeGenerator timeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(0, 30), LocalTime.of(1, 30), 10, anomalyDay + 1, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator4));

        return events;
    }

    private static List<AuthenticationEvent> prepareUserTimedEvents(String testUser, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator) throws GeneratorException {
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();

        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);

        // Generate events
        List<AuthenticationEvent> events = eventGenerator.generate();
        return events;
    }


}
