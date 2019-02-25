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


//Reconnaissance tools D: Destination processes: [qwinsta, tree, netsh, reg, forfiles, fsutil]
//NOTICE: It expect once a day with a day period in order to replace the userGenerator each day.
public class ReconToolGroupDEventGeneratorsBuilder extends UseCaseEventGeneratorsBuilder{
    public static final Pair[] RECON_TOOLS_GROUP_D = new Pair[] {
            Pair.of("qwinsta.exe","C:\\Windows\\System32"),
            Pair.of("tree","C:\\Windows\\System32"),
            Pair.of("tree.exe","C:\\Windows\\System32"),
            Pair.of("netsh","C:\\Windows\\System32"),
            Pair.of("netsh.exe","C:\\Windows\\System32"),
            Pair.of("reg.exe","C:\\Windows\\System32"),
            Pair.of("forfiles.exe","C:\\Windows\\System32"),
            Pair.of("fsutil.exe","C:\\Windows\\System32")
    };







    //Reconnaissance tools Group A. Normal behavior + abnormal time.
    private final int RECON_TOOL_GROUP_D_NUM_OF_NORMAL_USERS = 1;
    private final int RECON_TOOL_GROUP_D_NUM_OF_NORMAL_USERS_DAILY = 1;
    private final double RECON_TOOL_GROUP_D_PROBABILITY_NORMAL_USER = 0; // Not relevant for normal users
    private final int RECON_TOOL_GROUP_D_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS = 3600000;
    private final int RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 2;
    private final int RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 1;
    private final int RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 2;
    private final int RECON_TOOL_GROUP_D_NUM_OF_ADMIN_USERS = 750;
    private final int RECON_TOOL_GROUP_D_NUM_OF_ADMIN_USERS_DAILY = 600;
    private final double RECON_TOOL_GROUP_D_PROBABILITY_ADMIN_USER = 0.08; // ~480 events per hour per user
    private final int RECON_TOOL_GROUP_D_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS = 50000; //50 seconds. (2*3600/50)*0.08 =~11 users
    private final int RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 5;
    private final int RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 10;
    private final int RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 1;
    private final int RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 5;
    private final int RECON_TOOL_GROUP_D_NUM_OF_SERVICE_ACCOUNT_USERS = 1;
    private final int RECON_TOOL_GROUP_D_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 1;
    private final double RECON_TOOL_GROUP_D_PROBABILITY_SERVICE_ACCOUNT_USER = 0; // Not relevant to service accounts
    private final int RECON_TOOL_GROUP_D_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 60000; //Not really relevant since service accounts work all day.
    private final int RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;
    private final int RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;

    //Reconnaissance tools Group A. abnormal behavior.
    private final int ABNORMAL_RECON_TOOL_GROUP_D_NUM_OF_NORMAL_USERS_DAILY = 1;
    private final double ABNORMAL_RECON_TOOL_GROUP_D_PROBABILITY_NORMAL_USER = 0; // Not relevant for normal users
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 2;
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 1;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 2;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_D_NUM_OF_ADMIN_USERS_DAILY = 1;
    private final double ABNORMAL_RECON_TOOL_GROUP_D_PROBABILITY_ADMIN_USER = 0.0004; // ~1440 events per hour
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 5;
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 1;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 10;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_D_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 1;
    private final double ABNORMAL_RECON_TOOL_GROUP_D_PROBABILITY_SERVICE_ACCOUNT_USER = 0; // Not relevant to service accounts
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;//use all recon tool that you can


    @Override
    protected int getNumOfNormalUsers() {
        return RECON_TOOL_GROUP_D_NUM_OF_NORMAL_USERS;
    }

    @Override
    protected int getNumOfNormalUsersDaily() {
        return RECON_TOOL_GROUP_D_NUM_OF_NORMAL_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForNormalUsers() {
        return RECON_TOOL_GROUP_D_PROBABILITY_NORMAL_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForNormalUsers() {
        return RECON_TOOL_GROUP_D_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcesses() {
        return RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcesses() {
        return RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcesses() {
        return RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcesses() {
        return RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getNumOfAdminUsers() {
        return RECON_TOOL_GROUP_D_NUM_OF_ADMIN_USERS;
    }

    @Override
    protected int getNumOfAdminUsersDaily() {
        return RECON_TOOL_GROUP_D_NUM_OF_ADMIN_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForAdminUsers() {
        return RECON_TOOL_GROUP_D_PROBABILITY_ADMIN_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForAdminUsers() {
        return RECON_TOOL_GROUP_D_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcesses() {
        return RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcesses() {
        return RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcesses() {
        return RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcesses() {
        return RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getNumOfServiceAccountUsers() {
        return RECON_TOOL_GROUP_D_NUM_OF_SERVICE_ACCOUNT_USERS;
    }

    @Override
    protected int getNumOfServiceAccountUsersDaily() {
        return RECON_TOOL_GROUP_D_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsers() {
        return RECON_TOOL_GROUP_D_PROBABILITY_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForServiceAccountUsers() {
        return RECON_TOOL_GROUP_D_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcesses() {
        return RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcesses() {
        return RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected int getNumOfNormalUsersDailyForNonActiveWorkingHours() {
        return ABNORMAL_RECON_TOOL_GROUP_D_NUM_OF_NORMAL_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForNormalUsersForNonActiveWorkingHours() {
        return ABNORMAL_RECON_TOOL_GROUP_D_PROBABILITY_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getNumOfAdminUsersDailyForNonActiveWorkingHours() {
        return ABNORMAL_RECON_TOOL_GROUP_D_NUM_OF_ADMIN_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForAdminUsersForNonActiveWorkingHours() {
        return ABNORMAL_RECON_TOOL_GROUP_D_PROBABILITY_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getNumOfServiceAccountUsersDailyForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsersForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_PROBABILITY_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_D_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected List<FileEntity> getSrcProcesses() {
        return nonImportantProcesses;
    }

    @Override
    protected List<FileEntity> getDestProcesses() {
        return getReconToolGroupAFileEnities();
    }

    @Override
    protected String[] getOperationTypeNames() {
        String[] operationTypeNames = {PROCESS_OPERATION_TYPE.CREATE_PROCESS.value};
        return operationTypeNames;
    }

    @Override
    protected String getUseCaseTestName() {
        return "reconToolGroupA";
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
        return new ProcessCategoriesGenerator(new String[]{"RECONNAISSANCE_TOOL"});
    }

    @Override
    protected IStringListGenerator getDstProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessDirectoryGroupsGenerator(new String[]{"WINDOWS_SYSTEM32", "WINDOWS"});
    }

    public ReconToolGroupDEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
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


    private List<FileEntity> getReconToolGroupAFileEnities(){
        return getFileEnities(RECON_TOOLS_GROUP_D);
    }
}
