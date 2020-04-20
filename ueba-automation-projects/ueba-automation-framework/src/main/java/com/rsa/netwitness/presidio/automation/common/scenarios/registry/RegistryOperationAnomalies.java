package com.rsa.netwitness.presidio.automation.common.scenarios.registry;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.processentity.WindowsProcessEntityGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.List;

public class RegistryOperationAnomalies {

    public static List<RegistryEvent> getAbnormalProcessModifiedServiceKey(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal Process Modified a Service Key registry"
         *
         * Normal behavior: users are running a number 'common' processes that modify registry key in a group
         * Anomaly: some new process (by process full path) also modifies a service key
         *
         * Model:
         * Categorial Rarity Model:
         * context:registryKeyGroup && operationType == MODIFY_REGISTRY_VALUE
         * feature: processFilePath
         *
         */
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay);
        List<RegistryEvent> events = getNormalRegistryKeyChangeOperations(eventIdGen, timeGenerator, userGenerator);

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 15, anomalyDay, anomalyDay - 1);
        events.addAll(getAbnormalRegistryKeyChangeOperations(eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<RegistryEvent> getFutureAbnormalProcessModifiedServiceKey(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal Process Modified a Service Key registry"
         *
         * Normal behavior: users are running a number 'common' processes that modify registry key in a group
         * Anomaly: some new process (by process full path) also modifies a service key
         *
         * Model:
         * Categorial Rarity Model:
         * context:registryKeyGroup && operationType == MODIFY_REGISTRY_VALUE
         * feature: processFilePath
         *
         */
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, 30, 0);
        List<RegistryEvent> events = getNormalRegistryKeyChangeOperations(eventIdGen, timeGenerator, userGenerator);

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 15, -10, 0);
        events.addAll(getAbnormalRegistryKeyChangeOperations(eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<RegistryEvent> getNormalRegistryKeyChangeOperations(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, SingleUserGenerator userGenerator) throws GeneratorException {
        // List of processes used to change a key
        final Pair[] PROCESS_FILES = {
                Pair.of("normal_process_1.exe","C:\\Windows\\System32"),
                Pair.of("normal_process_2.exe","D:\\Windows\\System32"),
                Pair.of("normal_process_3.exe","C:\\Program Files (x86)\\Babylon\\Babylon-Pro"),
                Pair.of("normal_process_4.exe","C:\\Program Files\\DellTPad"),
        };

        // The process generator - given list of process files
        WindowsProcessEntityGenerator processEntityGenerator = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(PROCESS_FILES);
        processEntityGenerator.setProcessFileGenerator(processFileGenerator);

        return RegistryOperationActions.getRegistryKeyChangeOperations(processEntityGenerator, eventIdGen, timeGenerator, userGenerator);
    }

    public static List<RegistryEvent> getAbnormalRegistryKeyChangeOperations(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, SingleUserGenerator userGenerator) throws GeneratorException {
        // List of processes used to change a key
        final Pair[] PROCESS_FILES = {
                Pair.of("abnormal_process_x.exe","C:\\Program Files"),
                Pair.of("abnormal_process_x.exe","C:\\Program File"),
                Pair.of("abnormal_process_y.exe","C:\\Program Files"),
        };

        // The process generator - given list of process files
        WindowsProcessEntityGenerator processEntityGenerator = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(PROCESS_FILES);
        processEntityGenerator.setProcessFileGenerator(processFileGenerator);

        return RegistryOperationActions.getRegistryKeyChangeOperations(processEntityGenerator, eventIdGen, timeGenerator, userGenerator);
    }
}