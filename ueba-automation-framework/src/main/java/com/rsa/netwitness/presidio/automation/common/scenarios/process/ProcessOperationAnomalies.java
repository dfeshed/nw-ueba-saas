package com.rsa.netwitness.presidio.automation.common.scenarios.process;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.process.ProcessEventsGenerator;
import presidio.data.generators.user.NumberedUserCyclicGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessOperationAnomalies {

    public static List<ProcessEvent> getNormalProcessSchemaActivity(String testUser) throws GeneratorException {
        /**
         * Default events generator for process - for background events
         */
        List<ProcessEvent> events;

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 0), LocalTime.of(17, 00), 240, 28, 0);

        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        events = eventGenerator.generate();

        return events;
    }

    public static List<ProcessEvent> getMultipleNormalUsersActivity(String testUsersPrefix, int numberOfUsers) throws GeneratorException {
        List<ProcessEvent> events = new ArrayList<>();
        testUsersPrefix = testUsersPrefix + "_";
        for(int i=0 ; i < numberOfUsers ; i++) {
            String username = testUsersPrefix + i;
            events.addAll(getNormalProcessSchemaActivity(username));
        }

        return events;
    }

    public static List<ProcessEvent> getAbnormalProcessInjectedIntoLSASS(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal process injected into LSASS"
         *
         * Normal behavior: SOME PROCESS (src) performs operation "CREATE_REMOTE_THREAD" on dst process "C:\Windows\System32\lsass.exe"
         * Anomaly: some NEW PROCESS (by process full path) also performs "CREATE_REMOTE_THREAD" on dst process "C:\Windows\System32\lsass.exe"
         *
         * Model:
         * Categorial Rarity Model:
         * context: dstProcessFilePath == C:\Windows\System32\lsass.exe && operationType == CREATE_REMOTE_THREAD
         * feature: srcProcessFilePath
         *
         */
        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal behavior:
        final Pair[] NORMAL_PROCESS_FILES = {
                Pair.of("wininit.exe","C:\\Windows\\System32"),
                Pair.of("winlogon.exe","C:\\Windows\\System32"),
                Pair.of("svchost.exe","C:\\Windows\\System32"),
                Pair.of("taskhostw.exe","C:\\Windows\\System32"),
        };
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getProcessInjectedIntoLSASSOperations(NORMAL_PROCESS_FILES, eventIdGen, timeGenerator, userGenerator));

        //Anomaly:
        final Pair[] ABNORMAL_PROCESS_FILES = {
                Pair.of("explorer.exe","C:\\Windows\\System32"),
                Pair.of("services.exe","D:\\Windows\\System32"),
        };

        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(14, 30), 20, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getProcessInjectedIntoLSASSOperations(ABNORMAL_PROCESS_FILES, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getAbnormalProcessInjectedIntoLSASSFirstTime(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal process injected into LSASS"
         *
         * Normal behavior: SOME PROCESS (src) performs operation "CREATE_REMOTE_THREAD" on dst process different from lsaas.exe
         * Anomaly: some NEW PROCESS (by process full path) also performs "CREATE_REMOTE_THREAD" on dst process "C:\Windows\System32\lsass.exe"
         *
         * Model:
         * Categorial Rarity Model:
         * context: dstProcessFilePath == C:\Windows\System32\lsass.exe && operationType == CREATE_REMOTE_THREAD
         * feature: srcProcessFilePath
         *
         */
        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal behavior:
        final Pair[] NORMAL_PROCESS_FILES = {
                Pair.of("wininit.exe","C:\\Windows\\System32"),
                Pair.of("winlogon.exe","C:\\Windows\\System32"),
                Pair.of("svchost.exe","C:\\Windows\\System32"),
                Pair.of("taskhostw.exe","C:\\Windows\\System32"),
       };
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getProcessCreatedThread(NORMAL_PROCESS_FILES, eventIdGen, timeGenerator, userGenerator));

        //Anomaly:
        final Pair[] ABNORMAL_PROCESS_FILES = {
                Pair.of("runtimebroker.exe","C:\\Windows\\System32"),
                Pair.of("ABNORMAL1_" + testUser + ".exe","C:\\Windows\\System32"),
        };

        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(14, 30), 20, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getProcessInjectedIntoLSASSOperations(ABNORMAL_PROCESS_FILES, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getAbnormalProcessInjectedIntoWindowsProcess(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal process injected into Windows process"
         *
         * Normal behavior: SOME PROCESS (src) performs operation "CREATE_REMOTE_THREAD" on dst process with category WINDOWS_PROCESS
         * Anomaly: some NEW PROCESS (by process full path) also performs "CREATE_REMOTE_THREAD" on dst process WINDOWS_PROCESS
         *
         * Model:
         * Categorial Rarity Model:
         * context: dstProcessFilePath == C:\Windows\System32\taskhostw.exe && operationType == CREATE_REMOTE_THREAD
         * feature: srcProcessFilePath
         *
         */
        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal behavior:
        final Pair[] NORMAL_PROCESS_FILES = {
                Pair.of("wininit.exe","C:\\Windows\\System32"),
                Pair.of("svchost.exe","C:\\Windows\\System32"),
                Pair.of("winlogon.exe","C:\\Windows\\System32"),
                Pair.of("normal_process_4.exe","C:\\Program Files\\DellTPad"),
        };
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getProcessInjectedIntoWindowsOperations(NORMAL_PROCESS_FILES, eventIdGen, timeGenerator, userGenerator));

        //Anomaly:
        final Pair[] ABNORMAL_PROCESS_FILES = {
                Pair.of("tree.exe","C:\\Windows\\System32"),
                Pair.of("ABNORMAL_process_2.exe","D:\\Windows\\System32"),
        };

        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 20, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getProcessInjectedIntoWindowsOperations(ABNORMAL_PROCESS_FILES, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getAbnormalReconnaissanceTool(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal reconnaissance tool executed"
         *
         * Normal behavior: SOME PROCESS (src) performs operation "CREATE_PROCESS" on number of dst processes with category  "RECONNAISSANCE_TOOL"
         * Anomaly: the same SOME PROCESS  performs "CREATE_PROCESS" on ABNORMAL dst process with category  "RECONNAISSANCE_TOOL"
         *
         * Main model - Categorial Rarity Model:
         * context: userId, operationType= CREATE_PROCESS && dstProcessCategories = [RECONNAISSANCE_TOOL]
         * feature: dstProcessFilename
         *
         * Inverse model - Categorial Rarity Model:
         * context: dstProcessFilename, operationType= CREATE_PROCESS && dstProcessCategories = [RECONNAISSANCE_TOOL]
         * feature: userId
         *
         */

        final Pair[] DST_PROCESS_FILES = {
                Pair.of("arp.exe","C:\\Windows\\System32"),
                Pair.of("dsget.exe","D:\\Windows\\System32"),
        };

        final Pair[] DST_ABNORMAL_PROCESS_FILE = {
                Pair.of("reg.exe","C:\\temp"),
        };

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay);
        List<ProcessEvent> events = ProcessOperationActions.getReconnaissanceOperations(DST_PROCESS_FILES, eventIdGen, timeGenerator, userGenerator);

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 5, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(DST_ABNORMAL_PROCESS_FILE, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getAbnormalProcessExecutesScriptReducedScore(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal process executed a scripting tool"
         *
         * Normal behavior: testUser uses srcProcessFilePath1 to execute dstProcessFilename1 on 10 different days
         * Anomaly:         testUser uses srcProcessFilePath2 to execute the same dstProcessFilename1 on anomaly day
         *
         * For inverse model:
         * - testUser_other uses srcProcessFilePath2 to execute dstProcessFilename1 on 10 different days on 10 different days
         * - 4 more users (1% of all users) (testUser_custom1, testUser_custom2 ...) use srcProcessFilePath2 to execute dstProcessFilename1 once once, each on different day
         *
         */
        return getAbnormalExecScriptNoiseReduction(testUser, 4, anomalyDay);
    }

    public static List<ProcessEvent> getAbnormalProcessExecutesScriptHighScore(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal process executed a scripting tool"
         *
         * Normal behavior: testUser uses srcProcessFilePath1 to execute dstProcessFilename1 on 10 different days
         * Anomaly:         testUser uses srcProcessFilePath2 to execute the same dstProcessFilename1 on anomaly day
         *
         * For inverse model:
         * - testUser_other uses srcProcessFilePath2 to execute dstProcessFilename1 on 10 different days on 10 different days
         * - 2 more users (<1% of all users) (testUser_custom1, testUser_custom2) use srcProcessFilePath2 to execute dstProcessFilename1 once once, each on different day
         *
         */
        return getAbnormalExecScriptNoiseReduction(testUser, 2, anomalyDay);
    }

    public static List<ProcessEvent> getAbnormalExecScriptNoiseReduction(String testUser, int customUsersNum, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal process executed a scripting tool"
         *
         */
        final Pair[] DST_PROCESS_FILES = {
                Pair.of(testUser + "cscript.exe","C:\\Windows\\System32")
        };

        final Pair[] SRC_PROCESS_FILES = {
                Pair.of(testUser + "explorer.exe","C:\\Windows\\System32"),
        };

        final Pair[] SRC_ABNORMAL_PROCESS_FILE = {
                Pair.of(testUser + "services.exe","C:\\Windows\\System32"),
        };

        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // 10 normal days, main user, normal tool
        ITimeGenerator normalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 24 * 60, anomalyDay + 10, anomalyDay);
        events.addAll(ProcessOperationActions.getScriptingEngineCallOperations(SRC_PROCESS_FILES, DST_PROCESS_FILES, eventIdGen, normalTimeGenerator, userGenerator));

        // Anomaly: main user, abnormal tool, only abnormal day. Main scorer should give 100
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 10000, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getScriptingEngineCallOperations(SRC_ABNORMAL_PROCESS_FILE, DST_PROCESS_FILES, eventIdGen, abnormalTimeGenerator, userGenerator));


        // User users activity for inverse model score reduction
        // Other user uses abnormal tool during 10 days
        SingleUserGenerator otherUserGenerator = new SingleUserGenerator(testUser + "_other");
        ITimeGenerator otherUserTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 24 * 60, anomalyDay + 20, anomalyDay + 10);
        events.addAll(ProcessOperationActions.getScriptingEngineCallOperations(SRC_ABNORMAL_PROCESS_FILE, DST_PROCESS_FILES, eventIdGen, otherUserTimeGenerator, otherUserGenerator));

        // few different users use abnormal tool only once, each on different day - influence the noise reduction
        NumberedUserCyclicGenerator customUserGenerator = new NumberedUserCyclicGenerator(testUser + "_custom", customUsersNum, 1, 0);
        ITimeGenerator customUserTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 24 * 60, anomalyDay + 40, anomalyDay + 40 - customUsersNum);
        events.addAll(ProcessOperationActions.getScriptingEngineCallOperations(SRC_ABNORMAL_PROCESS_FILE, DST_PROCESS_FILES, eventIdGen, customUserTimeGenerator, customUserGenerator));

        return events;
    }

   public static List<ProcessEvent> getAbnormalReconToolLowScore(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal reconnaissance tool executed"
         *
         * Normal behavior: testUser uses recon1 on 10 different days
         * Anomaly:         testUser uses recon2 on anomaly day
         *
         * For inverse model:
         * - testUser_recon2 uses tool recon_2 on 10 different days
         * - 4 more users (1% of all users) (testUser_custom1, testUser_custom2, testUser_custom3) use recon_2 once, each on different day
         *
         */
        return getAbnormalReconToolNoiseReduction(testUser, 4, anomalyDay);
    }

    public static List<ProcessEvent> getAbnormalReconToolHighScore(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal reconnaissance tool executed"
         *
         * Normal behavior: testUser uses recon1 on 10 different days
         * Anomaly:         testUser uses recon2 on anomaly day
         *
         * For inverse model:
         * - testUser_recon2 uses tool recon_2 on 10 different days
         * - 2 more users (less then 1%) use recon_2 once, each on different day
         *
         */
        return getAbnormalReconToolNoiseReduction(testUser, 2, anomalyDay);
    }

    public static List<ProcessEvent> getAbnormalReconToolNoiseReduction(String testUser, int customUsersNum, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal reconnaissance tool executed"
         *
         * Normal behavior: testUser uses recon1 on 10 different days
         * Anomaly:         testUser uses recon2 on anomaly day
         *
         * For inverse model:
         * - testUser_recon2 uses tool recon_2 on 10 different days
         * - few custom users (testUser_custom1, testUser_custom2, testUser_custom2) use recon_2 once, each on different day
         *
         */

        final Pair[] DST_PROCESS_FILES = {
                Pair.of(testUser + "_recon1.exe","C:\\Windows\\"),
        };

        final Pair[] DST_ABNORMAL_PROCESS_FILE = {
                Pair.of(testUser + "_recon2.exe","C:\\Windows\\"),
        };

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // 10 normal days, main user, normal tool
        ITimeGenerator normalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 24 * 60, anomalyDay + 10, anomalyDay);
        List<ProcessEvent> events = ProcessOperationActions.getReconnaissanceOperations(DST_PROCESS_FILES, eventIdGen, normalTimeGenerator, userGenerator);

        // Anomaly: main user, abnormal tool, only abnormal day. Main scorer should give 100
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 10000, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(DST_ABNORMAL_PROCESS_FILE, eventIdGen, abnormalTimeGenerator, userGenerator));

        // User users activity for inverse model score reduction
        // Other user uses abnormal tool during 10 days
        SingleUserGenerator otherUserGenerator = new SingleUserGenerator(testUser + "_recon2");
        ITimeGenerator otherUserTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 24 * 60, anomalyDay + 20, anomalyDay + 10);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(DST_ABNORMAL_PROCESS_FILE, eventIdGen, otherUserTimeGenerator, otherUserGenerator));

        // few different users use abnormal tool only once, each on different day - influence the noise reduction
        NumberedUserCyclicGenerator customUserGenerator = new NumberedUserCyclicGenerator(testUser + "_custom", customUsersNum, 1, 0);
        ITimeGenerator customUserTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 24 * 60, anomalyDay + 40, anomalyDay + 40 - customUsersNum);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(DST_ABNORMAL_PROCESS_FILE, eventIdGen, customUserTimeGenerator, customUserGenerator));

        return events;
    }

    public static List<ProcessEvent> getReconnaissanceToolExecutedFirstTime(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal reconnaissance tool executed"
         *
         * Normal behavior: SOME PROCESS (src) performs operation "CREATE_PROCESS" on number of dst processes with category DIFFERENT from "RECONNAISSANCE_TOOL"
         * Anomaly: the same SOME PROCESS  performs "CREATE_PROCESS" on dst process with category  "RECONNAISSANCE_TOOL"
         *
         */

        final Pair[] DST_RECONNAISSANCE_TOOL = {
                Pair.of("ping.exe","C:\\Windows\\System32"),
                Pair.of("ipconfig.exe","C:\\Windows\\System32"),
                Pair.of("net.exe","C:\\Windows\\System32"),
        };

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay-1);
        List<ProcessEvent> events = ProcessOperationActions.getOfficeAppOperations(eventIdGen, timeGenerator, userGenerator);

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 10, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(DST_RECONNAISSANCE_TOOL, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getReconnaissanceToolUniqueExecutedFirstTime(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal reconnaissance tool executed"
         *
         * Normal behavior: SOME PROCESS (src) performs operation "CREATE_PROCESS" on number of dst processes with category DIFFERENT from "RECONNAISSANCE_TOOL"
         * Anomaly: the same SOME PROCESS  performs "CREATE_PROCESS" on dst process with category  "RECONNAISSANCE_TOOL"
         *
         */

        final Pair[] DST_RECONNAISSANCE_TOOL = {
                Pair.of("quser.exe","C:\\Windows\\System32"),
                Pair.of("ping","C:\\Windows\\System32"),
                Pair.of("net.exe","C:\\Windows\\System32"),
        };

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay-1);
        List<ProcessEvent> events = ProcessOperationActions.getOfficeAppOperations(eventIdGen, timeGenerator, userGenerator);

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 10, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getReconnaissanceOperations(DST_RECONNAISSANCE_TOOL, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getAbnormalProcessExecutesScript(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal process executed a Scripting Tool"
         *
         * Normal behavior: a number of usual processes (src) perform operation "CREATE_PROCESS" on number of dst processes with category  "SCRIPTING_ENGINE"
         * Anomaly: the same SOME ABNORMAL PROCESS (src)  performs "CREATE_PROCESS" on the same dst processes
         *
         *
         * First scorer:
         * context: dstProcessFilePath, operationType = CREATE_PROCESS && dstProcessCategories = [SCRIPTING_ENGINE]
         * scored: srcProcessFilePath
         *
         * Second scorer:
         * context: userId && dstProcessFilePath, operationType = CREATE_PROCESS && dstProcessCategories = [SCRIPTING_ENGINE]
         * scored: srcProcessFilePath
         *
         */

        final Pair[] DST_PROCESS_FILES = {
                Pair.of("cscript.exe","C:\\Windows\\System32"),
                Pair.of("mshta.exe","C:\\Windows\\System32"),
                Pair.of("powershell.exe","C:\\Program Files (x86)\\JavaScript"),
                Pair.of("wscript.exe","C:\\Program Files"),
        };

        final Pair[] SRC_PROCESS_FILES = {
                Pair.of("normal_src_process_1.exe","C:\\Windows\\System32"),
                Pair.of("normal_src_process_2.exe","D:\\Windows\\User\\MyTools"),
                Pair.of("normal_src_process_3.exe","C:\\Program Files"),
        };

        final Pair[] SRC_ABNORMAL_PROCESS_FILE = {
                Pair.of("ABNORMAL_SRC_PROCESS.exe","C:\\temp"),
        };

        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getScriptingEngineCallOperations(SRC_PROCESS_FILES, DST_PROCESS_FILES, eventIdGen, timeGenerator, userGenerator));

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 20, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getScriptingEngineCallOperations(SRC_ABNORMAL_PROCESS_FILE, DST_PROCESS_FILES, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getExecutesScriptFirstTime(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal process executed a Scripting Tool"
         *
         * Normal behavior: a number of usual processes (src) perform operation "CREATE_PROCESS" on number of dst processes with category "GAME"
         * Anomaly: the same SOME ABNORMAL PROCESS (src)  performs "CREATE_PROCESS" on the same dst processes with category "SCRIPTING_ENGINE"
         *
         *
         * First scorer:
         * context: dstProcessFilePath, operationType = CREATE_PROCESS && dstProcessCategories = [GAME]
         * scored: srcProcessFilePath
         *
         * Second scorer:
         * context: userId && dstProcessFilePath, operationType = CREATE_PROCESS && dstProcessCategories = [SCRIPTING_ENGINE]
         * scored: srcProcessFilePath
         *
         */

        final Pair[] DST_PROCESS_FILES = {
                Pair.of("tetris.exe","C:\\Program Files\\Games"),
                Pair.of("3dt.exe","C:\\Program Files\\Games"),
                Pair.of("solitaire.exe","C:\\Program Files\\Games"),
        };

        final Pair[] SRC_PROCESS_FILES = {
                Pair.of("explorer.exe","C:\\Windows\\System32"),
        };

        final Pair[] DST_ABNORMAL_PROCESS_FILES = {
                Pair.of("wscript.exe","C:\\Windows\\System32"),
        };

        final Pair[] SRC_ABNORMAL_PROCESS_FILE = {
                Pair.of("tetris1.exe","F:\\temp"),
        };

        final String[] SRC_NORMAL_PROCESS_CATEGORIES = new String[] { "GAME", "SCRIPTING_ENGINE" };

        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        SingleUserGenerator otherUserGenerator = new SingleUserGenerator(testUser + " (jr)");

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getCreateProcessOperation(SRC_PROCESS_FILES, DST_PROCESS_FILES, SRC_NORMAL_PROCESS_CATEGORIES, eventIdGen, timeGenerator, userGenerator));

        ITimeGenerator timeGeneratorOtherUser =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(9, 30), 45, anomalyDay + 15, anomalyDay+14);
        events.addAll(ProcessOperationActions.getCreateProcessOperation(SRC_PROCESS_FILES, DST_ABNORMAL_PROCESS_FILES, SRC_NORMAL_PROCESS_CATEGORIES, eventIdGen, timeGeneratorOtherUser, otherUserGenerator));

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 20, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getScriptingEngineCallOperations(SRC_ABNORMAL_PROCESS_FILE, DST_ABNORMAL_PROCESS_FILES, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ProcessEvent> getAbnormalAppTriggeredByScript(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal Application Triggered by Scripting Tool"
         *
         * Normal behavior: SOME USUAL PROCESS (src)  with category  "SCRIPTING_ENGINE" performs operation "CREATE_PROCESS" on number of dst processes
         * Anomaly: the same process (src)  performs "CREATE_PROCESS" on ABNORMAL DEST PROCESS
         *
         *
         * First scorer:
         * context: srcProcessFilePath, operationType = CREATE_PROCESS && srcProcessCategories = [SCRIPTING_ENGINE]
         * scored: dstProcessFilePath
         *
         * Second scorer:
         * context: userId && srcProcessFilePath, operationType = CREATE_PROCESS && srcProcessCategories = [SCRIPTING_ENGINE]
         * scored: dstProcessFilePath
         *
         */

        final Pair[] SRC_PROCESS_FILES = {
                Pair.of("cscript.exe","D:\\Windows\\PY"),
                Pair.of("mshta.exe","C:\\Program Files (x86)\\JavaScript"),
                Pair.of("powershell.exe","C:\\Program Files"),
        };

        final Pair[] DST_APPLICATION_FILES = {
                Pair.of("normal_executed_app_1.exe","C:\\Windows\\System32"),
                Pair.of("normal_executed_app_2.exe","D:\\Windows\\User\\MyTools"),
                Pair.of("normal_executed_app_3.exe","C:\\Program Files"),
        };

        final Pair[] DST_ABNORMAL_APPLICATION_FILE = {
                Pair.of("ABNORMAL_EXECUTED_APP.exe","C:\\temp"),
        };

        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getCalledByScriptingEngineOperations(SRC_PROCESS_FILES, DST_APPLICATION_FILES, eventIdGen, timeGenerator, userGenerator));

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 10, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getCalledByScriptingEngineOperations(SRC_PROCESS_FILES, DST_ABNORMAL_APPLICATION_FILE, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }
    public static List<ProcessEvent> getAbnormalProcessOpenedByScript(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Indicator display name: "Abnormal Application Triggered by Scripting Tool"
         *
         * Normal behavior: SOME USUAL PROCESS (src)  with category  "SCRIPTING_ENGINE" performs operation "OPEN_PROCESS" on number of dst processes
         * Anomaly: the same the same process (src)  performs "OPEN_PROCESS" on ABNORMAL DEST PROCESS
         *
         *
         * First scorer:
         * context: srcProcessFilePath, operationType = OPEN_PROCESS && srcProcessCategories = [SCRIPTING_ENGINE]
         * scored: dstProcessFilePath
         *
         * Second scorer:
         * context: userId && srcProcessFilePath, operationType = OPEN_PROCESS && srcProcessCategories = [SCRIPTING_ENGINE]
         * scored: dstProcessFilePath
         *
         */

        final Pair[] SRC_PROCESS_FILES = {
                Pair.of("cscript.exe","D:\\Windows\\System32"),
                Pair.of("mshta.exe","C:\\Windows\\PY"),
                Pair.of("powershell.exe","D:\\Program Files (x86)\\JavaScript"),
                Pair.of("wscript.exe","D:\\Program Files")
        };

        final Pair[] DST_PROCESS_FILES = {
                Pair.of("normal_opened_process_1.exe","C:\\Windows\\System32"),
                Pair.of("normal_opened_process_2.exe","C:\\Windows\\User\\MyTools"),
                Pair.of("normal_opened_process_3.exe","D:\\Program Files"),
        };

        final Pair[] DST_ABNORMAL_APPLICATION_FILE = {
                Pair.of("ABNORMAL_OPENED_APP.exe","D:\\temp"),
        };

        List<ProcessEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 30), LocalTime.of(18, 30), 55, anomalyDay + 40, anomalyDay);
        events.addAll(ProcessOperationActions.getOpenedByScriptingEngineOperations(SRC_PROCESS_FILES, DST_PROCESS_FILES, eventIdGen, timeGenerator, userGenerator));

        //Anomaly:
        ITimeGenerator abnormalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 30), LocalTime.of(18, 30), 10, anomalyDay, anomalyDay - 1);
        events.addAll(ProcessOperationActions.getOpenedByScriptingEngineOperations(SRC_PROCESS_FILES, DST_ABNORMAL_APPLICATION_FILE, eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }
}