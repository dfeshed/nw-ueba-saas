package presidio.data.generators.event.process;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.process.PROCESS_OPERATION_TYPE;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessCategoriesGenerator;
import presidio.data.generators.processentity.ProcessDirectoryGroupsGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;



//window processes
//NOTICE: It expect once a day with a day period in order to replace the userGenerator each day.
public class WindowsProcessesEventGeneratorsBuilder extends UseCaseEventGeneratorsBuilder{
    private static final Pair[] WINDOWS_PROCESS_FILES = {
            Pair.of("taskhostw.exe","C:\\Windows\\System32\\"),
            Pair.of("smss.exe","C:\\Windows\\System32\\"),
            Pair.of("services.exe","C:\\Windows\\System32\\"),
            Pair.of("lsaiso.exe","C:\\Windows\\System32\\")
    };



    //window processes. Normal behavior + abnormal time.
    private final int WINDOWS_PROCESS_INJECTED_NUM_OF_NORMAL_USERS = 90000;
    private final int WINDOWS_PROCESS_INJECTED_NUM_OF_NORMAL_USERS_DAILY = 70000;
    private final double WINDOWS_PROCESS_INJECTED_PROBABILITY_NORMAL_USER = 0.1; //~5 events per hour per user
    private final int WINDOWS_PROCESS_INJECTED_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS = 120000; //120 seconds. (8*3600/60)*0.1 =~13 users
    private final int WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 5;
    private final int WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 50;
    private final int WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_NORMAL_USER = 1;
    private final int WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_NORMAL_USER = 10;
    private final int WINDOWS_PROCESS_INJECTED_NUM_OF_ADMIN_USERS = 5000;
    private final int WINDOWS_PROCESS_INJECTED_NUM_OF_ADMIN_USERS_DAILY = 4900;
    private final double WINDOWS_PROCESS_INJECTED_PROBABILITY_ADMIN_USER = 0.136; // ~100 events per hour per user
    private final int WINDOWS_PROCESS_INJECTED_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS = 120000; //120 seconds. (2*3600/120)*0.136 = ~8 users
    private final int WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 5;
    private final int WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_ADMIN_USER = 1;
    private final int WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_ADMIN_USER = 10;
    private final int WINDOWS_PROCESS_INJECTED_NUM_OF_SERVICE_ACCOUNT_USERS = 100;
    private final int WINDOWS_PROCESS_INJECTED_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 20;
    private final double WINDOWS_PROCESS_INJECTED_PROBABILITY_SERVICE_ACCOUNT_USER = 0; // 0 events per day per user
    private final int WINDOWS_PROCESS_INJECTED_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 60000; //Not really relevant since service accounts work all day.
    private final int WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;
    private final int WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;

    //Windows processes abnormal behavior.
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_NUM_OF_NORMAL_USERS_DAILY = 10;
    private final double ABNORMAL_WINDOWS_PROCESS_INJECTED_PROBABILITY_NORMAL_USER = 0.00007; // ~25 events per hour per user
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 2;
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_NORMAL_USER = 1000;//use all windows processes that you can
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_NORMAL_USER = 2000;//use all window processes that you can
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_NUM_OF_ADMIN_USERS_DAILY = 2;
    private final double ABNORMAL_WINDOWS_PROCESS_INJECTED_PROBABILITY_ADMIN_USER = 0.0001; // ~180 events per hour
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1;
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 2;
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_ADMIN_USER = 1000;//use all window processes that you can
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_ADMIN_USER = 2000;//use all window processes that you can
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 1;
    private final double ABNORMAL_WINDOWS_PROCESS_INJECTED_PROBABILITY_SERVICE_ACCOUNT_USER = 0.000006; // ~22 events per hour per user
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;//use all window processes that you can
    private final int ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2000;//use all window processes that you can


    @Override
    protected int getNumOfNormalUsers() {
        return WINDOWS_PROCESS_INJECTED_NUM_OF_NORMAL_USERS;
    }

    @Override
    protected int getNumOfNormalUsersDaily() {
        return WINDOWS_PROCESS_INJECTED_NUM_OF_NORMAL_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForNormalUsers() {
        return WINDOWS_PROCESS_INJECTED_PROBABILITY_NORMAL_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForNormalUsers() {
        return WINDOWS_PROCESS_INJECTED_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcesses() {
        return WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcesses() {
        return WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcesses() {
        return WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcesses() {
        return WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getNumOfAdminUsers() {
        return WINDOWS_PROCESS_INJECTED_NUM_OF_ADMIN_USERS;
    }

    @Override
    protected int getNumOfAdminUsersDaily() {
        return WINDOWS_PROCESS_INJECTED_NUM_OF_ADMIN_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForAdminUsers() {
        return WINDOWS_PROCESS_INJECTED_PROBABILITY_ADMIN_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForAdminUsers() {
        return WINDOWS_PROCESS_INJECTED_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcesses() {
        return WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcesses() {
        return WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcesses() {
        return WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcesses() {
        return WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getNumOfServiceAccountUsers() {
        return WINDOWS_PROCESS_INJECTED_NUM_OF_SERVICE_ACCOUNT_USERS;
    }

    @Override
    protected int getNumOfServiceAccountUsersDaily() {
        return WINDOWS_PROCESS_INJECTED_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsers() {
        return WINDOWS_PROCESS_INJECTED_PROBABILITY_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForServiceAccountUsers() {
        return WINDOWS_PROCESS_INJECTED_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcesses() {
        return WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcesses() {
        return WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected int getNumOfNormalUsersDailyForNonActiveWorkingHours() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_NUM_OF_NORMAL_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForNormalUsersForNonActiveWorkingHours() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_PROBABILITY_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getNumOfAdminUsersDailyForNonActiveWorkingHours() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_NUM_OF_ADMIN_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForAdminUsersForNonActiveWorkingHours() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_PROBABILITY_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getNumOfServiceAccountUsersDailyForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsersForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_PROBABILITY_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MIN_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_WINDOWS_PROCESS_INJECTED_MAX_NUM_OF_WINDOWS_PROCESS_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected List<FileEntity> getSrcProcesses() {
        return nonImportantProcesses;
    }

    @Override
    protected List<FileEntity> getDestProcesses() {
        return getWindowProcessesFileEnities();
    }

    @Override
    protected String[] getOperationTypeNames() {
        String[] operationTypeNames = {PROCESS_OPERATION_TYPE.CREATE_REMOTE_THREAD.value};
        return operationTypeNames;
    }

    @Override
    protected String getUseCaseTestName() {
        return "windowsProcessesInjected";
    }



    @Override
    protected IStringListGenerator getSrcProcessCategoriesGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessCategoriesGenerator((List<String>) null);
    }

    @Override
    protected IStringListGenerator getSrcProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessDirectoryGroupsGenerator((List<String>) null);
    }

    @Override
    protected IStringListGenerator getDstProcessCategoriesGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessCategoriesGenerator(new String[]{"WINDOWS_PROCESS"});
    }

    @Override
    protected IStringListGenerator getDstProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessDirectoryGroupsGenerator(new String[]{"WINDOWS_SYSTEM32", "WINDOWS"});
    }

    public WindowsProcessesEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                                         IUserGenerator adminUserGenerator,
                                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                                         IUserGenerator serviceAccountUserGenerator,
                                                         List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                                         IMachineGenerator machineGenerator,
                                                         List<FileEntity> nonImportantProcesses){

        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange,
                adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange,
                serviceAccountUserGenerator, serviceAcountUserActivityRange,
                machineGenerator, nonImportantProcesses);
    }


    private List<FileEntity> getWindowProcessesFileEnities(){
        return getFileEnities(WINDOWS_PROCESS_FILES);
    }
}
