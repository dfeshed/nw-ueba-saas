package presidio.data.generators.event.performance.process;

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



//LSASS
//NOTICE: It expect once a day with a day period in order to replace the userGenerator each day.
public class LsassEventGeneratorsBuilder extends UseCaseEventGeneratorsBuilder {
    private static final Pair[] LSASS = {
            Pair.of("lsass.exe","C:\\Windows\\System32"),
            Pair.of("lsass","D:\\Windows\\System32")
    };



    //lsass injected Normal behavior + abnormal time.
    private final int LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 2;
    private final int LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_NORMAL_USER = 1;
    private final int LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_NORMAL_USER = 2;
    private final int LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_ADMIN_USER = 1;
    private final int LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_ADMIN_USER = 10;
    private final int LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;
    private final int LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2;

    //LSASS abnormal behavior.
    private final int ABNORMAL_LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1;
    private final int ABNORMAL_LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 2;
    private final int ABNORMAL_LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_NORMAL_USER = 1;
    private final int ABNORMAL_LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_NORMAL_USER = 2;
    private final int ABNORMAL_LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int ABNORMAL_LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int ABNORMAL_LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_ADMIN_USER = 1000;
    private final int ABNORMAL_LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_ADMIN_USER = 2000;
    private final int ABNORMAL_LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 100;
    private final int ABNORMAL_LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;
    private final int ABNORMAL_LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;
    private final int ABNORMAL_LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2000;



    public double getBuilderAllNormalUsersMultiplier(){
        return 0.5;
    }

    @Override
    protected double getPercentOfNormalUserPerDayOutOfTotalAmountOfUsers() {
        return 0.1;
    }

    @Override
    protected double getNumOfEventsPerNormalUserPerHourOnAvg() {
        return 5;
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
        return 0;
    }

    @Override
    protected double getPercentOfNormalUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.00001;
    }

    @Override
    protected double getNumOfEventsPerNormalUserWithAnomaliesPerHourOnAvg() {
        return 0;
    }

    @Override
    protected double getPercentOfAdminUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.00001;
    }

    @Override
    protected double getNumOfEventsPerAdminUserWithAnomaliesPerHourOnAvg() {
        return 500;
    }

    @Override
    protected double getPercentOfServiceAccountUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.00001;
    }

    @Override
    protected double getNumOfEventsPerServiceAccountUserWithAnomaliesPerHourOnAvg() {
        return 0;
    }



    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcesses() {
        return LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcesses() {
        return LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcesses() {
        return LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcesses() {
        return LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_NORMAL_USER;
    }



    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcesses() {
        return LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcesses() {
        return LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcesses() {
        return LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcesses() {
        return LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_ADMIN_USER;
    }



    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcesses() {
        return LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcesses() {
        return LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }




    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_NORMAL_USER;
    }



    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_ADMIN_USER;
    }



    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MIN_NUM_OF_LSASS_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return ABNORMAL_LSASS_INJECT_MAX_NUM_OF_LSASS_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected List<FileEntity> getSrcProcesses() {
        return nonImportantProcesses;
    }

    @Override
    protected List<FileEntity> getDestProcesses() {
        return getLsassFileEnities();
    }

    @Override
    protected String[] getOperationTypeNames() {
        String[] operationTypeNames = {PROCESS_OPERATION_TYPE.CREATE_REMOTE_THREAD.value};
        return operationTypeNames;
    }

    @Override
    protected String getUseCaseTestName() {
        return "lsassInjected";
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

    public LsassEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
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


    private List<FileEntity> getLsassFileEnities(){
        return getFileEnities(LSASS);
    }
}
