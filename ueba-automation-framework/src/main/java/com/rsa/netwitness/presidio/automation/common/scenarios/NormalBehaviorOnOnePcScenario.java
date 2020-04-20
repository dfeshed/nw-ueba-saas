package com.rsa.netwitness.presidio.automation.common.scenarios;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.event.dlpfile.DLPFileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.dlpfileop.DLPFileOperationGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.dlpfile.DLPFileEventsGenerator;
import presidio.data.generators.event.dlpfile.DLPFileEventsGeneratorTemplateFactory;
import presidio.data.generators.event.dlpfile.DriveTypePercentageGenerator;
import presidio.data.generators.event.dlpfile.ExecutingApplicationCyclicGenerator;
import presidio.data.generators.fileentity.FileSizeIncrementalGenerator;
import presidio.data.generators.machine.StaticIPMachineGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NormalBehaviorOnOnePcScenario {

     /**
     * user works normally between 8:00 and 16:00,
     * connects to one permanent source machine
     * performs move, copy and open operations
     */
    public static List<DLPFileEvent> generateEvents(String testUser, EntityEventIDFixedPrefixGenerator eventIdGen) throws GeneratorException {
        DLPFileEventsGenerator eventGenerator =
                new DLPFileEventsGeneratorTemplateFactory().getDLPFileEventSingleUserGenerator(testUser);

        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8,0), LocalTime.of(16,0), 10, 30, 3);
        eventGenerator.setTimeGenerator(myTimeGenerator);

        eventGenerator.setEventIDGenerator(eventIdGen);

        /**
         * User access 1 permanent machine
         */
        List<Pair<String,String>> pairs = new ArrayList<>();

        pairs.add(Pair.of("GINGERH-PC", "203.104.0.35"));

        StaticIPMachineGenerator machineGen = new StaticIPMachineGenerator(pairs);
        eventGenerator.setSourceMachineGenerator(machineGen);

        ExecutingApplicationCyclicGenerator appGen = new ExecutingApplicationCyclicGenerator(new String[] {"explorer.exe"});
        eventGenerator.setExecutingApplicationListGenerator(appGen);

        /**
         * No events for operations "DELETE" and "RECYCLE"
         */
        DLPFileOperationGenerator fileOpGen = new DLPFileOperationGenerator();
        FileSizeIncrementalGenerator fileSizeGen = new FileSizeIncrementalGenerator(50000,100000,15);
        fileOpGen.setFileSizeGenerator(fileSizeGen);
        eventGenerator.setFileOperationGenerator(fileOpGen);

        /**
         * Drive types - 90% fixed, 10% remote, 0% removable (no operations on removable drives)
         */
        String[] options = {"Fixed", "Remote", "Removable"};
        int[] percentage = {9,1,0};
        DriveTypePercentageGenerator driveTypeGen = new DriveTypePercentageGenerator(options,percentage);
        eventGenerator.setSourceDriveTypeGenerator(driveTypeGen);
        eventGenerator.setDestDriveTypeGenerator(driveTypeGen);

        // Generate events
        return eventGenerator.generate();
    }

}
