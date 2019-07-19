package com.rsa.netwitness.presidio.automation.common.scenarios.process;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.event.process.PROCESS_OPERATION_TYPE;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.StringListCyclicGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.process.CyclicOperationTypeGenerator;
import presidio.data.generators.event.process.ProcessEventsGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.processentity.ProcessCategoriesGenerator;
import presidio.data.generators.processentity.ProcessDirectoryGroupsGenerator;
import presidio.data.generators.processentity.WindowsProcessEntityGenerator;
import presidio.data.generators.processop.ProcessOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.common.helpers.UserNamesList.USER_NAMES;
import static java.util.Arrays.asList;

public class ProcessOperationActions {

    public static List<ProcessEvent> getEventsByProcessOperationName(String opName, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        String[] operationTypeNames = {opName};
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(operationTypeNames);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    /*********************************    Process operations:    *********************************/
    public static List<ProcessEvent> getCreateProcessOperation(Pair[] srcProcessFiles, Pair[] dstProcessFiles, String[] categories, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{PROCESS_OPERATION_TYPE.CREATE_PROCESS.value});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        // Source process with given files
        WindowsProcessEntityGenerator srcProcessEntryGenerator = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator srcProcessFileGenerator = new ProcessFileEntityGenerator(srcProcessFiles);
        srcProcessEntryGenerator.setProcessFileGenerator(srcProcessFileGenerator);

        opGenerator.setSourceProcessEntityGenerator(srcProcessEntryGenerator);

        // Destination process with given files and category
        WindowsProcessEntityGenerator destProcessEntryGenerator = new WindowsProcessEntityGenerator();

        if (categories != null) {
            ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(categories);
            destProcessEntryGenerator.setProcessCategoriesGenerator(categoriesGenerator);
        }

        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(dstProcessFiles);
        destProcessEntryGenerator.setProcessFileGenerator(processFileGenerator);

        opGenerator.setDestProcessEntityGenerator(destProcessEntryGenerator);

        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ProcessEvent> alertsSanityTestEvents(int historicalStartDay, int anomalyDay, int sequenceNo) throws GeneratorException {
        List<ProcessEvent> events = new ArrayList<>();

        final String testUser1 = USER_NAMES[USER_NAMES.length-1] + sequenceNo;
        final String testCase = "sanity_process_" + sequenceNo;

        /** 5 days of normal activity:
         * User 1, 2, 3:
         *
         * 3 days of anomalies:
         * User 1:
         * + abnormal time
         *
         * User 2:
         * + failed authentication - 3 times in hour
         * + USER_ACCOUNT_LOCKED
         * + USER_ACCOUNT_UNLOCKED
         * + PASSWORD_CHANGED_BY_NON_OWNER
         *
         * */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testCase);

        ITimeGenerator normalTimeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(3,0), LocalTime.of(21,59), 30, historicalStartDay, anomalyDay);
        ITimeGenerator abnormalTimeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(0,0), LocalTime.of(1,0), 5, anomalyDay, anomalyDay - 1);

        SingleUserGenerator userGenerator1 = new SingleUserGenerator(testUser1);

        // Normal:
        events.addAll(ProcessOperationActions.getEventsByProcessOperationName(PROCESS_OPERATION_TYPE.OPEN_PROCESS.value, eventIdGen, normalTimeGenerator1, userGenerator1));

        // Anomalies:
        events.addAll(ProcessOperationActions.getEventsByProcessOperationName(PROCESS_OPERATION_TYPE.CREATE_PROCESS.value, eventIdGen, abnormalTimeGenerator1, userGenerator1));

        // and other anomalies for multiple users (by sequrnceNo)
        events.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoWindowsProcess("proc_win_" + sequenceNo, anomalyDay));

        return events;
    }

    public static List<ProcessEvent> getProcessInjectedIntoWindowsOperations(Pair[] processFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * For given generators:
         * - time,
         * - user,
         * - source process
         *
         * Create events of "CREATE_REMOTE_THREAD" operations with destination process category WINDOWS_PROCESS
         *
         * */

        final Pair[] WINDOWS_PROCESS_FILES = { Pair.of("taskhostw.exe"," C:\\Windows\\System32\\"),
                                            Pair.of("smss.exe"," C:\\Windows\\System32\\"),
                                            Pair.of("lsass.exe"," C:\\Windows\\System32\\"),
                                            Pair.of("services.exe"," C:\\Windows\\System32\\"),
                                            Pair.of("lsaiso.exe"," C:\\Windows\\System32\\") };

        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is "CREATE_REMOTE_THREAD"
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{"CREATE_REMOTE_THREAD"});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        WindowsProcessEntityGenerator WindowsProcessEntityGenerator = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(processFiles);
        WindowsProcessEntityGenerator.setProcessFileGenerator(processFileGenerator);

        opGenerator.setSourceProcessEntityGenerator(WindowsProcessEntityGenerator);

        // The registry entry - given list of entries
        presidio.data.generators.processentity.WindowsProcessEntityGenerator destProcessEntryGenerator = new WindowsProcessEntityGenerator();

        // destination process - some important windows process
        ProcessFileEntityGenerator destProcessFileGenerator = new ProcessFileEntityGenerator(WINDOWS_PROCESS_FILES);
        destProcessEntryGenerator.setProcessFileGenerator(destProcessFileGenerator);

        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"WINDOWS_PROCESS"});
        destProcessEntryGenerator.setProcessCategoriesGenerator(categoriesGenerator);

        opGenerator.setDestProcessEntityGenerator(destProcessEntryGenerator);
        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ProcessEvent> getProcessInjectedIntoWindowsOperations(WindowsProcessEntityGenerator srcWindowsProcessEntityGenerator, WindowsProcessEntityGenerator destWindowsProcessEntityGenerator, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * For given generators:
         * - source process
         * - destination process
         * - eventId
         * - time
         * - user
         *
         * Create events of "CREATE_REMOTE_THREAD" operations with destination process category WINDOWS_PROCESS
         * */

        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is "CREATE_REMOTE_THREAD"
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{"CREATE_REMOTE_THREAD"});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        // Destination process category is "important windows process"
        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"WINDOWS_PROCESS"});
        destWindowsProcessEntityGenerator.setProcessCategoriesGenerator(categoriesGenerator);

        opGenerator.setSourceProcessEntityGenerator(srcWindowsProcessEntityGenerator);
        opGenerator.setDestProcessEntityGenerator(destWindowsProcessEntityGenerator);

        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ProcessEvent> getProcessInjectedIntoLSASSOperations(Pair[] processFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * For given generators:
         * - time,
         * - user,
         * - source process
         *
         * Create events of "CREATE_REMOTE_THREAD" operations that have fixed destination process - "lsass.exe",
         * destination process directory group - "WINDOWS_SYSTEM32"
         * destination process category - "WINDOWS_PROCESS"
         *
         * */

        final Pair[] LSASS_PROCESS_FILE = { Pair.of("lsass.exe"," C:\\Windows\\System32\\") };

        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is "CREATE_REMOTE_THREAD"
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{"CREATE_REMOTE_THREAD"});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        WindowsProcessEntityGenerator WindowsProcessEntityGenerator = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(processFiles);
        WindowsProcessEntityGenerator.setProcessFileGenerator(processFileGenerator);

        opGenerator.setSourceProcessEntityGenerator(WindowsProcessEntityGenerator);

        // The registry entry - given list of entries
        presidio.data.generators.processentity.WindowsProcessEntityGenerator destProcessEntryGenerator = new WindowsProcessEntityGenerator();

        // destination process - lsass.exe
        ProcessFileEntityGenerator destProcessFileGenerator = new ProcessFileEntityGenerator(LSASS_PROCESS_FILE);
        destProcessEntryGenerator.setProcessFileGenerator(destProcessFileGenerator);

        // destination directory group - WINDOWS_SYSTEM32
        ProcessDirectoryGroupsGenerator dstProcessDirectoryGroupsGenerator = new ProcessDirectoryGroupsGenerator(new String[]{"WINDOWS_SYSTEM32", "WINDOWS"});
        destProcessEntryGenerator.setProcessDirectoryGroupsGenerator(dstProcessDirectoryGroupsGenerator);

        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"WINDOWS_PROCESS"});
        destProcessEntryGenerator.setProcessCategoriesGenerator(categoriesGenerator);

        opGenerator.setDestProcessEntityGenerator(destProcessEntryGenerator);
        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ProcessEvent> getProcessCreatedThread(Pair[] processFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * For given generators:
         * - time,
         * - user,
         * - source process
         *
         * Create events of "CREATE_REMOTE_THREAD" operations that have fixed destination process - "some_thread_process.exe",
         * destination process directory group - "SOME_GROUP"
         * destination process category - "WINDOWS_PROCESS"
         *
         * */

        final Pair[] PROCESS_FILE = { Pair.of("some_thread_process.exe"," C:\\Windows\\MyApp\\") };

        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is "CREATE_REMOTE_THREAD"
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{"CREATE_REMOTE_THREAD"});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        WindowsProcessEntityGenerator WindowsProcessEntityGenerator = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(processFiles);
        WindowsProcessEntityGenerator.setProcessFileGenerator(processFileGenerator);

        opGenerator.setSourceProcessEntityGenerator(WindowsProcessEntityGenerator);

        // The registry entry - given list of entries
        presidio.data.generators.processentity.WindowsProcessEntityGenerator destProcessEntryGenerator = new WindowsProcessEntityGenerator();

        // destination process
        ProcessFileEntityGenerator destProcessFileGenerator = new ProcessFileEntityGenerator(PROCESS_FILE);
        destProcessEntryGenerator.setProcessFileGenerator(destProcessFileGenerator);

        // destination directory group - SOME_GROUP
        ProcessDirectoryGroupsGenerator dstProcessDirectoryGroupsGenerator = new ProcessDirectoryGroupsGenerator(new String[]{"SOME_GROUP"});
        destProcessEntryGenerator.setProcessDirectoryGroupsGenerator(dstProcessDirectoryGroupsGenerator);

        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"WINDOWS_PROCESS"});
        destProcessEntryGenerator.setProcessCategoriesGenerator(categoriesGenerator);

        opGenerator.setDestProcessEntityGenerator(destProcessEntryGenerator);
        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ProcessEvent> getReconnaissanceOperations(Pair<String, String>[] dstProcesses, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * Generate events with:
         * - destination process category: RECONNAISSANCE_TOOL
         * - given list of destination processes (File path/file name pairs)
         *
         * **/
        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is "CREATE_PROCESS"
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{"CREATE_PROCESS"});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        WindowsProcessEntityGenerator destProcessEntryGenerator = new WindowsProcessEntityGenerator();

        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"RECONNAISSANCE_TOOL"});
        destProcessEntryGenerator.setProcessCategoriesGenerator(categoriesGenerator);

        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(dstProcesses);
        destProcessEntryGenerator.setProcessFileGenerator(processFileGenerator);

        // destination directory group - WINDOWS_SYSTEM32
        ProcessDirectoryGroupsGenerator dstProcessDirectoryGroupsGenerator = new ProcessDirectoryGroupsGenerator(new String[]{"WINDOWS_SYSTEM32", "WINDOWS"});
        destProcessEntryGenerator.setProcessDirectoryGroupsGenerator(dstProcessDirectoryGroupsGenerator);

        opGenerator.setDestProcessEntityGenerator(destProcessEntryGenerator);

        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ProcessEvent> getOfficeAppOperations(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * Generate events with:
         * - operation "CREATE_PROCESS"
         * - given list of destination processes (File path/file name pairs)
         *
         * **/

        final Pair[] DST_PROCESS_FILES = {
                Pair.of("WINWORD.EXE","C:\\Program Files\\Microsoft Office\\root\\Office16\\"),
                Pair.of("POWERPNT.EXE","C:\\Program Files\\Microsoft Office\\root\\Office16\\"),
                Pair.of("lync.exe","C:\\Program Files\\Microsoft Office\\root\\Office16\\"),
                Pair.of("excelcnv.exe","C:\\Program Files\\Microsoft Office\\root\\Office16\\"),
                Pair.of("excel.exe","C:\\Program Files\\Microsoft Office\\root\\Office16\\"),
                Pair.of("visio.exe","C:\\Program Files\\Microsoft Office\\root\\Office16\\"),
        };

        final List<List<String>> appCategoriesList = asList(
                asList("OFFICE", "WORD_PROCESSOR"),
                asList("OFFICE"),
                asList("OFFICE", "COMMUNICATOR" ),
                asList("OFFICE", "SPREADSHEET" ),
                asList("OFFICE", "SPREADSHEET" ),
                asList("OFFICE" ));

        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is "CREATE_PROCESS"
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{"CREATE_PROCESS"});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        WindowsProcessEntityGenerator destProcessEntryGenerator = new WindowsProcessEntityGenerator();

        StringListCyclicGenerator categoriesGenerator = new StringListCyclicGenerator((List<String>[]) appCategoriesList.toArray());
        destProcessEntryGenerator.setProcessCategoriesGenerator(categoriesGenerator);

        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(DST_PROCESS_FILES);
        destProcessEntryGenerator.setProcessFileGenerator(processFileGenerator);

        opGenerator.setDestProcessEntityGenerator(destProcessEntryGenerator);

        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }
    public static List<ProcessEvent> getScriptingEngineCallOperations(Pair[] srcProcessFiles, Pair[] dstProcessFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        return getDestScriptingEngineOperations("CREATE_PROCESS",  srcProcessFiles, dstProcessFiles, eventIdGen, timeGenerator, userGenerator);
    }
    public static List<ProcessEvent> getCalledByScriptingEngineOperations(Pair[] srcProcessFiles, Pair[] dstProcessFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        return getSrcScriptingEngineOperations("CREATE_PROCESS",  srcProcessFiles, dstProcessFiles, eventIdGen, timeGenerator, userGenerator);
    }

    public static List<ProcessEvent> getScriptingEngineOpenOperations(Pair[] srcProcessFiles, Pair[] dstProcessFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        return getDestScriptingEngineOperations("OPEN_PROCESS",  srcProcessFiles, dstProcessFiles, eventIdGen, timeGenerator, userGenerator);
    }

    public static List<ProcessEvent> getOpenedByScriptingEngineOperations(Pair[] srcProcessFiles, Pair[] dstProcessFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        return getSrcScriptingEngineOperations("OPEN_PROCESS",  srcProcessFiles, dstProcessFiles, eventIdGen, timeGenerator, userGenerator);
    }

    public static List<ProcessEvent> getDestScriptingEngineOperations(String operationType, Pair[] srcProcessFiles, Pair[] dstProcessFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * Generate events with:
         * - destination process category: SCRIPTING_ENGINE
         * - given list of destination processes (File path/file name pairs)
         *
         * **/
        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is <operationType>
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{operationType});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        // Source process with given files
        WindowsProcessEntityGenerator srcProcessEntryGenerator = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator srcProcessFileGenerator = new ProcessFileEntityGenerator(srcProcessFiles);
        srcProcessEntryGenerator.setProcessFileGenerator(srcProcessFileGenerator);

        opGenerator.setSourceProcessEntityGenerator(srcProcessEntryGenerator);

        // Destination process with given files and category
        WindowsProcessEntityGenerator destProcessEntryGenerator = new WindowsProcessEntityGenerator();
        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"SCRIPTING_ENGINE"});
        destProcessEntryGenerator.setProcessCategoriesGenerator(categoriesGenerator);
        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(dstProcessFiles);
        destProcessEntryGenerator.setProcessFileGenerator(processFileGenerator);

        opGenerator.setDestProcessEntityGenerator(destProcessEntryGenerator);

        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }
    public static List<ProcessEvent> getSrcScriptingEngineOperations(String operationType, Pair[] srcProcessFiles, Pair[] dstProcessFiles, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * Generate events with:
         * - source process category: SCRIPTING_ENGINE
         * - given list of destination processes (File path/file name pairs)
         *
         * **/
        ProcessEventsGenerator eventGenerator = new ProcessEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is <operationType>
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{operationType});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        // Source process with given files
        WindowsProcessEntityGenerator srcProcessEntryGenerator = new WindowsProcessEntityGenerator();
        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"SCRIPTING_ENGINE"});
        srcProcessEntryGenerator.setProcessCategoriesGenerator(categoriesGenerator);
        ProcessFileEntityGenerator srcProcessFileGenerator = new ProcessFileEntityGenerator(srcProcessFiles);
        srcProcessEntryGenerator.setProcessFileGenerator(srcProcessFileGenerator);

        opGenerator.setSourceProcessEntityGenerator(srcProcessEntryGenerator);

        // Destination process with given files and category
        WindowsProcessEntityGenerator destProcessEntryGenerator = new WindowsProcessEntityGenerator();
        ProcessFileEntityGenerator processFileGenerator = new ProcessFileEntityGenerator(dstProcessFiles);
        destProcessEntryGenerator.setProcessFileGenerator(processFileGenerator);

        opGenerator.setDestProcessEntityGenerator(destProcessEntryGenerator);

        eventGenerator.setProcessOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }
}
