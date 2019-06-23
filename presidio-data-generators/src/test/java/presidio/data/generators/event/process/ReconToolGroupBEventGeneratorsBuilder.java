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


//Reconnaissance tools B: Destination processes: [whoami, quser, qprocess, tasklist]
//NOTICE: It expect once a day with a day period in order to replace the userGenerator each day.
public class ReconToolGroupBEventGeneratorsBuilder extends UseCaseEventGeneratorsBuilder{
    public static final Pair[] RECON_TOOLS_GROUP_B = new Pair[] {
            Pair.of("whoami","C:\\Windows\\System32"),
            Pair.of("whoami.exe","C:\\Windows\\System32"),
            Pair.of("quser.exe","C:\\Windows\\System32"),
            Pair.of("qprocess.exe","C:\\Windows\\System32"),
            Pair.of("tasklist.exe","C:\\Windows\\System32")
    };







    //Reconnaissance tools Group A. Normal behavior + abnormal time.
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 2;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 2;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 5;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 10;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 5;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;

    //Reconnaissance tools Group A. abnormal behavior.
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 2;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 1;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 2;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 5;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 1;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 10;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;//use all recon tool that you can

    public double getBuilderAllNormalUsersMultiplier(){
        return 0.0000000000001;
    }

    @Override
    protected double getPercentOfNormalUserPerDayOutOfTotalAmountOfUsers() {
        return 1;
    }

    @Override
    protected double getNumOfEventsPerNormalUserPerHourOnAvg() {
        return 0;
    }

    public double getBuilderAllAdminUsersMultiplier(){
        return 0.15;
    }

    @Override
    protected double getPercentOfAdminUserPerDayOutOfTotalAmountOfUsers() {
        return 0.1;
    }

    @Override
    protected double getNumOfEventsPerAdminUserPerHourOnAvg() {
        return 480;
    }

    public double getBuilderAllServiceAccountUsersMultiplier(){
        return 0.0000000000001;
    }

    @Override
    protected double getPercentOfServiceAccountUserPerDayOutOfTotalAmountOfUsers() {
        return 1;
    }

    @Override
    protected double getNumOfEventsPerServiceAccountUserPerHourOnAvg() {
        return 0;
    }

    @Override
    protected double getPercentOfNormalUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 1;
    }

    @Override
    protected double getNumOfEventsPerNormalUserWithAnomaliesPerHourOnAvg() {
        return 0;
    }

    @Override
    protected double getPercentOfAdminUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.002;
    }

    @Override
    protected double getNumOfEventsPerAdminUserWithAnomaliesPerHourOnAvg() {
        return 1500;
    }

    @Override
    protected double getPercentOfServiceAccountUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 1;
    }

    @Override
    protected double getNumOfEventsPerServiceAccountUserWithAnomaliesPerHourOnAvg() {
        return 0;
    }



    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcesses() {
        return RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcesses() {
        return RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcesses() {
        return RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcesses() {
        return RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcesses() {
        return RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcesses() {
        return RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcesses() {
        return RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcesses() {
        return RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcesses() {
        return RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcesses() {
        return RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER;
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

    public ReconToolGroupBEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
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
        return getFileEnities(RECON_TOOLS_GROUP_B);
    }
}
