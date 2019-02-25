package presidio.data.generators.event.process;

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


public class NonImportantProcessEventGeneratorsBuilder extends UseCaseEventGeneratorsBuilder{

    /** Probabilities of events for different user groups
     * Calculated as: events_per_day / milliseconds_per_activity_period_in_a_day
     * Examples:
     * 1. 94500 users make 300 events per day in 16 active hours (6:00 - 22:00)
     * 94500*300         = 28350000    events per day
     * 16*60*60*1000    = 57,600,000  millisecond per day in users activity interval
     * 28350000/57600000  = 0.4921875
     *
     * 2. 5k users make 100 events per hour at 22 active hours
     * 5k * 100 * 22  = 11M   events per day
     * 22*60*60*1000 = 79.2M
     * 11M/79.2M = 0.3189
     *
     *
     * 3. 500 users make 500 events per hour at 24 active hours
     * 6M events per day
     * 6M / 86.4M = 0.06944
     *
     * !!! scenario with 10 users making 50K events per hour causes issue with garbage collector in hourly_output_processor !!!
     * ####10 * 50000 * 10 / 36000000 = 0.1389
     * **/

    private final double PROBABILITY_NORMAL_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.4921875;
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS = 50000; //50 seconds. (8*3600/50)*0.49 =~280 users
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 100;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 200;

    private final double PROBABILITY_ADMIN_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.3189;
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS = 50000; //50 seconds. (2*3600/50)*0.3189 =~45 users
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;

    private final double PROBABILITY_SERVICE_ACCOUNT_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.06944;
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 1; //Not really relevant since service accounts work all day.
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;



    @Override
    protected int getNumOfNormalUsers() {
        return 94500;
    }

    @Override
    protected int getNumOfNormalUsersDaily() {
        return 70000;
    }

    @Override
    protected double getEventProbabilityForNormalUsers() {
        return PROBABILITY_NORMAL_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForNormalUsers() {
        return TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcesses() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcesses() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcesses() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcesses() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getNumOfAdminUsers() {
        return 5000;
    }

    @Override
    protected int getNumOfAdminUsersDaily() {
        return 4900;
    }

    @Override
    protected double getEventProbabilityForAdminUsers() {
        return PROBABILITY_ADMIN_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForAdminUsers() {
        return TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcesses() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcesses() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcesses() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcesses() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getNumOfServiceAccountUsers() {
        return 500;
    }

    @Override
    protected int getNumOfServiceAccountUsersDaily() {
        return 500;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsers() {
        return PROBABILITY_SERVICE_ACCOUNT_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForServiceAccountUsers() {
        return TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcesses() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcesses() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcesses() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected int getNumOfNormalUsersDailyForNonActiveWorkingHours() {
        return 180;
    }

    @Override
    protected double getEventProbabilityForNormalUsersForNonActiveWorkingHours() {
        return 0.00081;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMinNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER;
    }

    @Override
    protected int getNumOfAdminUsersDailyForNonActiveWorkingHours() {
        return 13;
    }

    @Override
    protected double getEventProbabilityForAdminUsersForNonActiveWorkingHours() {
        return 0.001625;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMinNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER;
    }

    @Override
    protected int getNumOfServiceAccountUsersDailyForAbnormalEvents() {
        return 2;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsersForAbnormalEvents() {
        return 0.0005;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents() {
        return MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER;
    }


    @Override
    protected List<FileEntity> getSrcProcesses() {
        return nonImportantProcesses;
    }

    @Override
    protected List<FileEntity> getDestProcesses() {
        return nonImportantProcesses;
    }

    @Override
    protected String[] getOperationTypeNames() {
        String[] operationTypeNames = {PROCESS_OPERATION_TYPE.OPEN_PROCESS.value, PROCESS_OPERATION_TYPE.CREATE_PROCESS.value, PROCESS_OPERATION_TYPE.CREATE_REMOTE_THREAD.value};
        return operationTypeNames;
    }

    @Override
    protected String getUseCaseTestName() {
        return "NonImportantProcesses";
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
        return new ProcessCategoriesGenerator((List<String>) null);
    }

    @Override
    protected IStringListGenerator getDstProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessDirectoryGroupsGenerator((List<String>) null);
    }

    public NonImportantProcessEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
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



}
