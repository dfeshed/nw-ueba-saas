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



//Reconnaissance tools C: Destination processes: [nltest, route, ping, netdom, dsget, dsquery]
//NOTICE: It expect once a day with a day period in order to replace the userGenerator each day.
public class ReconToolGroupCEventGeneratorsBuilder extends UseCaseEventGeneratorsBuilder{
    public static final Pair[] RECON_TOOLS_GROUP_C = new Pair[] {
            Pair.of("nltest.exe","C:\\Windows\\System32"),
            Pair.of("route","C:\\Windows\\System32"),
            Pair.of("route.exe","C:\\Windows\\System32"),
            Pair.of("ping","C:\\Windows\\System32"),
            Pair.of("ping.exe","C:\\Windows\\System32"),
            Pair.of("netdom.exe","C:\\Windows\\System32"),
            Pair.of("dsget.exe","C:\\Windows\\System32"),
            Pair.of("dsquery.exe","C:\\Windows\\System32")
    };



    //Reconnaissance tools Group C. Normal behavior + abnormal time.
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 50;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 10;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 10;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;
    private final int RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;

    //Reconnaissance tools Group C. abnormal behavior.
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1000;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 5000;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 1000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 2000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 1000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 2000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 100;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_B_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2000;//use all recon tool that you can

    public double getBuilderAllNormalUsersMultiplier(){
        return 0.23;
    }

    @Override
    protected double getPercentOfNormalUserPerDayOutOfTotalAmountOfUsers() {
        return 0.35;
    }

    @Override
    protected double getNumOfEventsPerNormalUserPerHourOnAvg() {
        return 8;
    }

    public double getBuilderAllAdminUsersMultiplier(){
        return 0.6;
    }

    @Override
    protected double getPercentOfAdminUserPerDayOutOfTotalAmountOfUsers() {
        return 0.66;
    }

    @Override
    protected double getNumOfEventsPerAdminUserPerHourOnAvg() {
        return 110;
    }

    public double getBuilderAllServiceAccountUsersMultiplier(){
        return 0.2;
    }

    @Override
    protected double getPercentOfServiceAccountUserPerDayOutOfTotalAmountOfUsers() {
        return 0.2;
    }

    @Override
    protected double getNumOfEventsPerServiceAccountUserPerHourOnAvg() {
        return 0.05;
    }

    @Override
    protected double getPercentOfNormalUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.002;
    }

    @Override
    protected double getNumOfEventsPerNormalUserWithAnomaliesPerHourOnAvg() {
        return 24;
    }

    @Override
    protected double getPercentOfAdminUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.002;
    }

    @Override
    protected double getNumOfEventsPerAdminUserWithAnomaliesPerHourOnAvg() {
        return 330;
    }

    @Override
    protected double getPercentOfServiceAccountUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.002;
    }

    @Override
    protected double getNumOfEventsPerServiceAccountUserWithAnomaliesPerHourOnAvg() {
        return 7;
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

    public ReconToolGroupCEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
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
        return getFileEnities(RECON_TOOLS_GROUP_C);
    }
}
