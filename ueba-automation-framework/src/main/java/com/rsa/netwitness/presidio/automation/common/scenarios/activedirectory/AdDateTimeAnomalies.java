package com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory;

import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.activedirectory.ActiveDirectoryEventsGenerator;
import presidio.data.generators.user.SingleUserGenerator;
import com.rsa.netwitness.presidio.automation.common.scenarios.TimeScenarioTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AdDateTimeAnomalies {

    public static List<ActiveDirectoryEvent> getAbnormalClose2NormalActivity(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        // most of events are during "normal" work hours
        List<ActiveDirectoryEvent> events = prepareUserTimedEvents(testUser, eventIdGen, TimeScenarioTemplate.getNormalTimeGenerator());

        // add low % of normal events at times close to normal work hours
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(7, 45), LocalTime.of(7, 46), 1, anomalyDay + 28, anomalyDay + 25);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator1));
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(7, 55), LocalTime.of(7, 56), 1, anomalyDay + 25, anomalyDay + 20);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator2));

        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 5), LocalTime.of(16, 6), 1, anomalyDay + 20, anomalyDay + 16);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator3));

        ITimeGenerator timeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 10), LocalTime.of(16, 11), 1, anomalyDay + 16, anomalyDay + 13);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator4));

        // abnormal events - for 2 last days
        // 1h 10min before usual - score 2
        ITimeGenerator abnTimeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(6, 50), LocalTime.of(7, 00), 1, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnTimeGenerator1));

        // 1h 5min after usual, score 31
        ITimeGenerator abnTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(17, 5), LocalTime.of(17, 9), 1, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnTimeGenerator2));

        // 1h 15min after usual, score 89
        ITimeGenerator abnTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(17, 15), LocalTime.of(17, 20), 1, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnTimeGenerator3));

        return events;
    }

    public static List<ActiveDirectoryEvent> getAbnormalFarFromNormalActivity(String testUser, int anomalyDay) throws GeneratorException {
        // anomaly events at 18:00, 20:00, 22:00 - score 0
        return getAbnormalFarFromNormalActivity(testUser, anomalyDay, LocalTime.of(18, 00), LocalTime.of(22, 10), 120);
    }

    public static List<ActiveDirectoryEvent> getAbnormalFarFromNormalActivity(String testUser, int anomalyDay, LocalTime anomalyStartTime, LocalTime anomalyEndTime, int intervalMin) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        List<ActiveDirectoryEvent> events = prepareUserTimedEvents(testUser, eventIdGen, TimeScenarioTemplate.getNormalTimeGenerator());

        // add normal events during 24 hours
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(0, 00), LocalTime.of(23, 59), 60, anomalyDay + 10, anomalyDay + 9);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator1));

        ITimeGenerator abnormalTimeGenerator1 =
                new MinutesIncrementTimeGenerator(anomalyStartTime, anomalyEndTime, intervalMin, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnormalTimeGenerator1));

        return events;
    }
    public static List<ActiveDirectoryEvent> getAbnormalTimeActivity(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        List<ActiveDirectoryEvent> events = new ArrayList<>();

        // add normal events during 24 hours
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(0, 00), LocalTime.of(23, 59), 60, anomalyDay + 33, anomalyDay + 3);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator1));

        // add time anomaly events at 18:00, 20:00, 22:00 - score 0
        ITimeGenerator abnormalTimeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(18, 00), LocalTime.of(22, 10), 120, anomalyDay + 3, anomalyDay + 2);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnormalTimeGenerator1));

        return events;
    }

    public static List<ActiveDirectoryEvent> getMultipleNormalUsersActivity(String testUsersPrefix, int numberOfUsers) throws GeneratorException {
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        testUsersPrefix = testUsersPrefix + "_";
        for(int i=0 ; i < numberOfUsers ; i++) {
            String username = testUsersPrefix + i;
            events.addAll(getNormalTimeActivity(username));
        }

        return events;
    }

    public static List<ActiveDirectoryEvent> getNormalTimeActivity(String testUser) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        List<ActiveDirectoryEvent> events = new ArrayList<>();

        // add normal events during 24 hours
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(16, 30), 60, 35, 0);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));
        return events;

    }

    private static List<ActiveDirectoryEvent> prepareUserTimedEvents(String testUser, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();

        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        eventGenerator.setUserGenerator(userGenerator);

        eventGenerator.setTimeGenerator(timeGenerator);

        eventGenerator.setEventIdGenerator(eventIdGen);

        // Generate events
        List<ActiveDirectoryEvent> events = eventGenerator.generate();
        return events;
    }
}
