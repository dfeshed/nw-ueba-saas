package com.rsa.netwitness.presidio.automation.common.scenarios.file;

import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.user.SingleUserGenerator;
import com.rsa.netwitness.presidio.automation.common.scenarios.TimeScenarioTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FileDateTimeAnomalies {

    public static List<FileEvent> getAbnormalTimeOnMinNormalSamples(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        List<FileEvent> events = prepareUserTimedEvents(testUser, eventIdGen, TimeScenarioTemplate.getMinSamplesTimeGenerator());

        // add time anomaly
        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 30), LocalTime.of(20, 0), 60, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, myTimeGenerator));

        return events;
    }

    public static List<FileEvent> getAbnormalTimeActivity(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        List<FileEvent> events = prepareUserTimedEvents(testUser, eventIdGen, TimeScenarioTemplate.getNormalTimeGenerator());

        // add time anomaly
        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 30), LocalTime.of(19, 0), 90, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, myTimeGenerator));

        return events;
    }

    public static List<FileEvent> getMultipleNormalUsersActivity(String testUsersPrefix, int numberOfUsers) throws GeneratorException {
        List<FileEvent> events = new ArrayList<>();
        testUsersPrefix = testUsersPrefix + "_";
        for(int i=0 ; i < numberOfUsers ; i++) {
            String username = testUsersPrefix + i;
            events.addAll(getNormalTimeActivity(username));
        }

        return events;
    }

    public static List<FileEvent> getFrequentAbnormalTimeActivity(String testUser, int anomalyDay) throws GeneratorException {
        return getFrequentAbnormalTimeActivity(testUser, 6, anomalyDay, anomalyDay - 1);
    }

    public static List<FileEvent> getFrequentAbnormalTimeActivity(String testUser, int historicalStartDay, int anomalyStartDay, int anomalyEndDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 30), LocalTime.of(23, 0), 10, historicalStartDay, anomalyEndDay);
        List<FileEvent> events = prepareUserTimedEvents(testUser, eventIdGen, timeGenerator);

        // add time anomaly
        ITimeGenerator abnormalTimeGenerator =
                new TimeGenerator(LocalTime.of(16, 30), LocalTime.of(18, 30), 3000, anomalyStartDay, anomalyEndDay);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnormalTimeGenerator));

        return events;
    }

    public static List<FileEvent> getNormalTimeActivity(String testUser) throws GeneratorException {
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        List<FileEvent> events = prepareUserTimedEvents(testUser, eventIdGen, TimeScenarioTemplate.getNormalTimeGenerator());
        return events;
    }

    public static List<FileEvent> getNormalAndAbnormalTimeActivity(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        List<FileEvent> events = new ArrayList<>();

        // normal - 40-3 db
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 0), LocalTime.of(21, 30), 90, 40, anomalyDay);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));

        // add time anomaly  3 db
        timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(11, 0), 30, anomalyDay, anomalyDay-1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));

        return events;
    }

    public static List<FileEvent> getTimeActivity(String testUser, ITimeGenerator timeGenerator) throws GeneratorException {
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        return prepareUserTimedEvents(testUser, eventIdGen, timeGenerator);
    }

    public static List<FileEvent> normalTimeGenerator(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        List<FileEvent> events = new ArrayList<>();

        // add time anomaly 2-1 db
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(11, 0), 10, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));

        return events;
    }

    public static List<FileEvent> getAbnormalNearborderTimeDeviation(String testUser, int anomalyDay) throws GeneratorException {
        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        List<FileEvent> events = prepareUserTimedEvents(testUser, eventIdGen, TimeScenarioTemplate.getNormalTimeGenerator());

        // add time anomaly 1 - ~10%: 2 events between 6 and 8, 2 events between 16 and 18
        ITimeGenerator abnormalTimeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(18, 00), LocalTime.of(20, 00), 60, anomalyDay + 10, anomalyDay + 2);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnormalTimeGenerator1));

        ITimeGenerator abnormalTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(5, 30), LocalTime.of(7, 30), 60, anomalyDay + 10, anomalyDay + 2);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnormalTimeGenerator2));

        // add time anomaly 2
        ITimeGenerator abnormalTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 00), LocalTime.of(23, 00), 20, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnormalTimeGenerator3));

        // add time anomaly 2
        ITimeGenerator abnormalTimeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(4, 30), LocalTime.of(8, 00), 20, anomalyDay, anomalyDay - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, abnormalTimeGenerator4));

        return events;
    }

    private static List<FileEvent> prepareUserTimedEvents(String testUser, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator) throws GeneratorException {
        myTimeGenerator.reset();
        FileEventsGenerator eventGenerator = new FileEventsGenerator();

        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // Generate events
        return eventGenerator.generate();
    }


    public static List<FileEvent> getFileSmartWithDateTimeAnomaly(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some file rename actions
         * Anomaly: user performs large number of file rename actions from local drive to local drive
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some rename file operations
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 30, anomalyDay);
        events.addAll(FileOperationActions.getEventsByOperationName("DeleteFileOperation", eventIdGen, timeGenerator1, userGenerator));

        // Abnormal - many rename file operations
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(21, 00), LocalTime.of(21, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("RenameFileOperation", eventIdGen, timeGenerator2, userGenerator));
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(21, 30), LocalTime.of(22, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFolderManyDistinctOperation", eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<FileEvent> getAbnormalTimeActivity4Smart(String testUser, int anomalyDAy) throws GeneratorException {
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        List<FileEvent> events = new ArrayList<>();

        // normal time activity
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(17, 0), 90, 40, anomalyDAy + 8);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));

        // anomaly on daysback 10-9
        timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 30), LocalTime.of(18, 0), 30, anomalyDAy + 8, anomalyDAy + 7);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));

        timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 31), LocalTime.of(18, 0), 40, anomalyDAy + 8, anomalyDAy + 7);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));

        timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 32), LocalTime.of(17, 0), 15, anomalyDAy + 8, anomalyDAy + 7);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));

        // anomaly on daysback 2-1
        timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 30), LocalTime.of(18, 0), 1, anomalyDAy, anomalyDAy - 1);
        events.addAll(prepareUserTimedEvents(testUser, eventIdGen, timeGenerator));

        return events;
    }


}
