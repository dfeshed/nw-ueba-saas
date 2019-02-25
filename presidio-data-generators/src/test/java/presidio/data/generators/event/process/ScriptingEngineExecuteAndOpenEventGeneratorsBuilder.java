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



//scripting engine
//NOTICE: It expect once a day with a day period in order to replace the userGenerator each day.
public class ScriptingEngineExecuteAndOpenEventGeneratorsBuilder extends UseCaseEventGeneratorsBuilder{
    private static final Pair[] SCRIPTING_ENGINE = {
            Pair.of("cscript.exe","C:\\Windows\\System32"),
            Pair.of("mshta.exe","D:\\Windows\\System32"),
            Pair.of("powershell.exe","D:\\Windows\\System32\\WindowsPowerShell\\v1.0"),
            Pair.of("wscript.exe","D:\\Windows\\System32")
    };



    //Scripting engine execute. Normal behavior + abnormal time.
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_NORMAL_USERS = 10000;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_NORMAL_USERS_DAILY = 5000;
    private final double SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_NORMAL_USER = 0.028; //~20 events per hour per user
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS = 60000; //60 seconds. (8*3600/60)*0.028 =~13 users
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 50;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_NORMAL_USER = 1;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_NORMAL_USER = 10;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_ADMIN_USERS = 3000;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_ADMIN_USERS_DAILY = 2000;
    private final double SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_ADMIN_USER = 0.06; // ~108 events per hour per user
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS = 50000; //50 seconds. (2*3600/60)*0.06 =~7 users
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_ADMIN_USER = 1;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_ADMIN_USER = 10;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_SERVICE_ACCOUNT_USERS = 100;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 20;
    private final double SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_SERVICE_ACCOUNT_USER = 0; // 0 events per day per user
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 60000; //Not really relevant since service accounts work all day.
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;

    //Scripting Engine abnormal behavior.
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_NORMAL_USERS_DAILY = 10;
    private final double ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_NORMAL_USER = 0.00014; // ~50 events per hour per user
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1000;
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 5000;
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_NORMAL_USER = 1000;//use all scripting engine that you can
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_NORMAL_USER = 2000;//use all scripting engine that you can
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_ADMIN_USERS_DAILY = 2;
    private final double ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_ADMIN_USER = 0.0001; // ~180 events per hour
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_ADMIN_USER = 1000;//use all scripting engine that you can
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_ADMIN_USER = 2000;//use all scripting engine that you can
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 1;
    private final double ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_SERVICE_ACCOUNT_USER = 0.000006; // ~22 events per hour per user
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 100;
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;//use all scripting engine that you can
    private final int ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2000;//use all scripting engine that you can


    @Override
    protected int getNumOfNormalUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_NORMAL_USERS;
    }

    @Override
    protected int getNumOfNormalUsersDaily() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_NORMAL_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForNormalUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_NORMAL_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForNormalUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getNumOfAdminUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_ADMIN_USERS;
    }

    @Override
    protected int getNumOfAdminUsersDaily() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_ADMIN_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForAdminUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_ADMIN_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForAdminUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getNumOfServiceAccountUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_SERVICE_ACCOUNT_USERS;
    }

    @Override
    protected int getNumOfServiceAccountUsersDaily() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForServiceAccountUsers() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected int getNumOfNormalUsersDailyForNonActiveWorkingHours() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_NORMAL_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForNormalUsersForNonActiveWorkingHours() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getNumOfAdminUsersDailyForNonActiveWorkingHours() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_ADMIN_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForAdminUsersForNonActiveWorkingHours() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getNumOfServiceAccountUsersDailyForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsersForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_PROBABILITY_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MIN_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_SCRIPTING_ENGINE_EXECUTE_AND_OPEN_MAX_NUM_OF_SCRIPTING_ENGINE_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected List<FileEntity> getDestProcesses() {
        return nonImportantProcesses;
    }

    @Override
    protected List<FileEntity> getSrcProcesses() {
        return getScriptingEngineFileEnities();
    }

    @Override
    protected String[] getOperationTypeNames() {
        String[] operationTypeNames = {PROCESS_OPERATION_TYPE.OPEN_PROCESS.value, PROCESS_OPERATION_TYPE.CREATE_PROCESS.value};
        return operationTypeNames;
    }

    @Override
    protected String getUseCaseTestName() {
        return "scriptingEngineExecute";
    }



    @Override
    protected IStringListGenerator getDstProcessCategoriesGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessCategoriesGenerator((List<String>) null);
    }

    @Override
    protected IStringListGenerator getDstProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessDirectoryGroupsGenerator((List<String>) null);
    }

    @Override
    protected IStringListGenerator getSrcProcessCategoriesGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessCategoriesGenerator(new String[]{"SCRIPTING_ENGINE"});
    }

    @Override
    protected IStringListGenerator getSrcProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessDirectoryGroupsGenerator(new String[]{"WINDOWS_SYSTEM32", "WINDOWS"});
    }

    public ScriptingEngineExecuteAndOpenEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
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


    private List<FileEntity> getScriptingEngineFileEnities(){
        return getFileEnities(SCRIPTING_ENGINE);
    }
}
