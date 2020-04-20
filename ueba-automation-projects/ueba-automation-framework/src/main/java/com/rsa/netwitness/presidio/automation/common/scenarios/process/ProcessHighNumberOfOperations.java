package com.rsa.netwitness.presidio.automation.common.scenarios.process;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessHighNumberOfOperations {

    public static final Pair[] RECON_TOOLS = new Pair[] {
            Pair.of("arp","C:\\Windows\\System32"),
            Pair.of("arp.exe","C:\\Windows\\System32"),
            Pair.of("dsget.exe","C:\\Windows\\System32"),
            Pair.of("dsquery.exe","C:\\Windows\\System32"),
            Pair.of("forfiles.exe","C:\\Windows\\System32"),
            Pair.of("fsutil.exe","C:\\Windows\\System32"),
            Pair.of("hostname","C:\\Windows\\System32"),
            Pair.of("hostname.exe","C:\\Windows\\System32"),
            Pair.of("ipconfig.exe","C:\\Windows\\System32"),
            Pair.of("net","C:\\Windows\\System32"),
            Pair.of("net.exe","C:\\Windows\\System32"),
            Pair.of("netdom.exe","C:\\Windows\\System32"),
            Pair.of("netsh","C:\\Windows\\System32"),
            Pair.of("netsh.exe","C:\\Windows\\System32"),
            Pair.of("netstat","C:\\Windows\\System32"),
            Pair.of("netstat.exe","C:\\Windows\\System32"),
            Pair.of("nbtstat.exe","C:\\Windows\\System32"),
            Pair.of("nltest.exe","C:\\Windows\\System32"),
            Pair.of("ping","C:\\Windows\\System32"),
            Pair.of("ping.exe","C:\\Windows\\System32"),
            Pair.of("quser.exe","C:\\Windows\\System32"),
            Pair.of("qprocess.exe","C:\\Windows\\System32"),
            Pair.of("qwinsta.exe","C:\\Windows\\System32"),
            Pair.of("reg.exe","C:\\Windows\\System32"),
            Pair.of("route","C:\\Windows\\System32"),
            Pair.of("route.exe","C:\\Windows\\System32"),
            Pair.of("sc.exe","C:\\Windows\\System32"),
            Pair.of("systeminfo.exe","C:\\Windows\\System32"),
            Pair.of("tasklist.exe","C:\\Windows\\System32"),
            Pair.of("tree","C:\\Windows\\System32"),
            Pair.of("tree.exe","C:\\Windows\\System32"),
            Pair.of("whoami","C:\\Windows\\System32"),
            Pair.of("whoami.exe","C:\\Windows\\System32"),
            Pair.of("wmic.exe","C:\\Windows\\System32")
    };
    
    public static List<ProcessEvent> getHighNumOfReconnaissanceTools(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         *  a set of reconnaissance tools used every day with low frequency (1 event per hour)
         * Anomaly:
         *  the same reconnaissance tools used frequently during abnormal hour (60 events per hour)
         */
        Pair<String, String>[] normalTools = new Pair[] { RECON_TOOLS[10], RECON_TOOLS[11], RECON_TOOLS[12] };

        List<ProcessEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(normalTools, eventIdGen, timeGenerator1, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(normalTools, eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getHighNumOfDistinctReconnaissanceTools(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         *  a set of reconnaissance tools used every day with low frequency (1 event per hour)
         * Anomaly:
         *  many different reconnaissance tools used by the same user during few abnormal hours (60 events per hour)
         */

        Pair<String, String>[] normalTools = new Pair[] { RECON_TOOLS[20], RECON_TOOLS[21], RECON_TOOLS[22], RECON_TOOLS[23], RECON_TOOLS[24] };

        List<ProcessEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(normalTools, eventIdGen, timeGenerator1, userGenerator));

        //Anomaly - get all supported recon tools, some of them will be abnormal for this user:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 00), LocalTime.of(16, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(RECON_TOOLS, eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getHighNumOfReconnaissanceToolsByUserAndTarget(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         *  2 users run 5 reconnaissance tools each every day with low frequency (1 event per hour)

         * Anomaly:
         *  one of the users run one of reconnaissance tools frequently during abnormal hour (20 events per hour)
         *  (other user continues to work with the same frequency)
         *
         */

        Pair<String, String>[] normalTools = new Pair[] { RECON_TOOLS[0], RECON_TOOLS[1], RECON_TOOLS[2], RECON_TOOLS[3], RECON_TOOLS[4] };

        List<ProcessEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        // Normal:
        SingleUserGenerator userGenerator1 = new SingleUserGenerator(testUser + "_a");
        SingleUserGenerator userGenerator2 = new SingleUserGenerator(testUser + "_b");

        ITimeGenerator normalTimeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 40, anomalyDay);

        ITimeGenerator normalTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(16, 0), 60, anomalyDay + 40, anomalyDay);

        events.addAll(ProcessOperationActions.getReconnaissanceOperations(normalTools, eventIdGen, normalTimeGenerator1, userGenerator1));
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(normalTools, eventIdGen, normalTimeGenerator2, userGenerator2));

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(16, 30), 3, anomalyDay, anomalyDay - 1);
        ITimeGenerator normalTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(16, 30), 60, anomalyDay, anomalyDay - 1);

        events.addAll(ProcessOperationActions.getReconnaissanceOperations(Arrays.copyOfRange(normalTools, 2, 3), eventIdGen, abnormalTimeGenerator1, userGenerator1));
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(normalTools, eventIdGen, normalTimeGenerator3, userGenerator1));
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(normalTools, eventIdGen, normalTimeGenerator3, userGenerator2));

        return events;
    }

    /**   Future scenarios for testing active users        **/

    public static List<ProcessEvent> getFutureHighNumOfDistinctReconnaissanceTools(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         *  a set of reconnaissance tools used every day with low frequency (1 event per hour)
         * Anomaly:
         *  many different reconnaissance tools used by the same user during few abnormal hours (60 events per hour)
         */

        Pair<String, String>[] normalTools = new Pair[] { RECON_TOOLS[20], RECON_TOOLS[21], RECON_TOOLS[22], RECON_TOOLS[23], RECON_TOOLS[24] };

        List<ProcessEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 40, anomalyDay - 2 );
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(normalTools, eventIdGen, timeGenerator1, userGenerator));

        //Anomaly - get all supported recon tools, some of them will be abnormal for this user:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 00), LocalTime.of(16, 30), 1, anomalyDay-2 , anomalyDay - 6);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(RECON_TOOLS, eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

}
