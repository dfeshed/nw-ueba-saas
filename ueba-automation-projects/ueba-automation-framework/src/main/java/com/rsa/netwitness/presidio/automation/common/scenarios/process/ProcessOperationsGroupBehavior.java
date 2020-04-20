package com.rsa.netwitness.presidio.automation.common.scenarios.process;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.processentity.WindowsProcessEntityGenerator;
import presidio.data.generators.user.NumberedUserCyclicGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessOperationsGroupBehavior {

    /**
     * Group 1: fan
     * 300 users, each run source 1 of ~17~ process that injects into one of ~7~ destination process per day
     *      * this should create a rarity model that will not give a score for new user that runs some custom process injecting into another custom process?
     *
     *
     *
     * **/


    public static List<ProcessEvent> getGroupProcessGroupEvents(String groupPrefix, int usersNum, int srcProcessesNum, int dstProcessesNum, LocalTime startTime, LocalTime endTime, int intervalMin, int daysBackFrom, int daysBackTo) throws GeneratorException {
        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(groupPrefix);
        NumberedUserCyclicGenerator userGenerator = new NumberedUserCyclicGenerator(groupPrefix, usersNum, 1, 0);

        Pair[] srcProcessFiles = new Pair[srcProcessesNum];
        for (int i = 0; i < srcProcessesNum; i++){
            srcProcessFiles[i] = Pair.of(groupPrefix + i + "src.exe", "c:\\windows\\system32\\" + i);
        }

        Pair[] dstProcessFiles = new Pair[dstProcessesNum];
        for (int i = 0; i < dstProcessesNum; i++){
            dstProcessFiles[i] = Pair.of(groupPrefix + i + "dst.exe", "c:\\windows\\system32\\" + i);
        }

        WindowsProcessEntityGenerator srcProcessEntityGen = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator srcProcessFileGenerator = new ProcessFileEntityGenerator(srcProcessFiles);
        srcProcessEntityGen.setProcessFileGenerator(srcProcessFileGenerator);

        WindowsProcessEntityGenerator dstProcessEntityGen = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator dstProcessFileGenerator = new ProcessFileEntityGenerator(dstProcessFiles);
        dstProcessEntityGen.setProcessFileGenerator(dstProcessFileGenerator);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(startTime, endTime, intervalMin, daysBackFrom, daysBackTo);
        events.addAll(ProcessOperationActions.getProcessInjectedIntoWindowsOperations(srcProcessEntityGen, dstProcessEntityGen, eventIdGen, timeGenerator, userGenerator));

        return events;
    }

}
