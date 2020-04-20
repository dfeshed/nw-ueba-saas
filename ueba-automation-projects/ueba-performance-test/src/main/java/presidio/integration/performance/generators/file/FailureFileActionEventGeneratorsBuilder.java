package presidio.integration.performance.generators.file;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.event.performance.file.FileUseCaseEventGeneratorsBuilder;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;

public class FailureFileActionEventGeneratorsBuilder extends FileUseCaseEventGeneratorsBuilder{

    private static final double PERCENT_OF_NORMAL_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.01d;
    private static final int NUM_OF_EVENTS_PER_NORMAL_USER_PER_HOUR_ON_AVG = 5;

    private static final double PERCENT_OF_ADMIN_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.01d;
    private static final int NUM_OF_EVENTS_PER_ADMIN_USER_PER_HOUR_ON_AVG = 5;

    private static final double PERCENT_OF_SERVICE_ACCOUNT_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 1d;
    private static final int NUM_OF_EVENTS_PER_SERVICE_ACCOUNT_USER_PER_HOUR_ON_AVG = 0;

    private static final double PERCENT_OF_NORMAL_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.001d;
    private static final int NUM_OF_EVENTS_PER_NORMAL_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 80;

    private static final double PERCENT_OF_ADMIN_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.001d;
    private static final int NUM_OF_EVENTS_PER_ADMIN_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 800;

    private static final double PERCENT_OF_SERVICE_ACCOUNT_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.001d;
    private static final int NUM_OF_EVENTS_PER_SERVICE_ACCOUNT_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 200;

    private List<OperationType> operationTypesPool;

    public FailureFileActionEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                                   List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                                   List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                                   IUserGenerator adminUserGenerator,
                                                   List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                                   List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                                   IUserGenerator serviceAccountUserGenerator,
                                                   List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                                   IMachineGenerator normalUserSrcMachinesGenerator,
                                                   IMachineGenerator normalUserAbnormalSrcMachinesGenerator,
                                                   IMachineGenerator adminUserSrcMachinesGenerator,
                                                   IMachineGenerator adminUserAbnormalSrcMachinesGenerator,
                                                   IMachineGenerator serviceAccountUserSrcMachinesGenerator,
                                                   IMachineGenerator serviceAccountUserAbnormalSrcMachinesGenerator,
                                                   IFileEntityGenerator normalUserSrcFileEntitiesGenerator,
                                                   IFileEntityGenerator normalUserAbnormalSrcFileEntitiesGenerator,
                                                   IFileEntityGenerator adminUserSrcFileEntitiesGenerator,
                                                   IFileEntityGenerator adminUserAbnormalSrcFileEntitiesGenerator,
                                                   IFileEntityGenerator serviceAccountUserSrcFileEntitiesGenerator,
                                                   IFileEntityGenerator serviceAccountUserAbnormalSrcFileEntitiesGenerator,
                                                   IFileEntityGenerator normalUserDstFileEntitiesGenerator,
                                                   IFileEntityGenerator normalUserAbnormalDstFileEntitiesGenerator,
                                                   IFileEntityGenerator adminUserDstFileEntitiesGenerator,
                                                   IFileEntityGenerator adminUserAbnormalDstFileEntitiesGenerator,
                                                   IFileEntityGenerator serviceAccountUserDstFileEntitiesGenerator,
                                                   IFileEntityGenerator serviceAccountUserAbnormalDstFileEntitiesGenerator) throws GeneratorException {
        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange, adminUserGenerator,
                adminUserActivityRange, adminUserAbnormalActivityRange, serviceAccountUserGenerator,
                serviceAcountUserActivityRange, normalUserSrcMachinesGenerator, normalUserAbnormalSrcMachinesGenerator,
                adminUserSrcMachinesGenerator, adminUserAbnormalSrcMachinesGenerator,
                serviceAccountUserSrcMachinesGenerator, serviceAccountUserAbnormalSrcMachinesGenerator,
                normalUserSrcFileEntitiesGenerator, normalUserAbnormalSrcFileEntitiesGenerator,
                adminUserSrcFileEntitiesGenerator, adminUserAbnormalSrcFileEntitiesGenerator,
                serviceAccountUserSrcFileEntitiesGenerator, serviceAccountUserAbnormalSrcFileEntitiesGenerator,
                normalUserDstFileEntitiesGenerator, normalUserAbnormalDstFileEntitiesGenerator,
                adminUserDstFileEntitiesGenerator, adminUserAbnormalDstFileEntitiesGenerator,
                serviceAccountUserDstFileEntitiesGenerator, serviceAccountUserAbnormalDstFileEntitiesGenerator);
    }

    private List<OperationType> getOperationTypesPool(){
        if(operationTypesPool == null){
            operationTypesPool = getAllOperationTypes(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION);
        }
        return operationTypesPool;
    }

    @Override
    protected String getUseCaseTestName() {
        return "FailedFileActionEvent";
    }

    @Override
    protected IOperationTypeGenerator getOperationTypeGeneratorForNormalbehavior() {
        UserOperationTypeGenerator generator =
                new UserOperationTypeGenerator(
                        getOperationTypesPool(),
                        getMinNumOfOperationTypesForNormalbehavior(),
                        getMaxNumOfOperationTypesForNormalbehavior());
        return generator;
    }

    private int getMinNumOfOperationTypesForNormalbehavior(){
        return 1;
    }

    private int getMaxNumOfOperationTypesForNormalbehavior(){
        return getOperationTypesPool().size();
    }

    @Override
    protected IOperationTypeGenerator getOperationTypeGeneratorForAbnormalBehavior() {
        UserOperationTypeGenerator generator =
                new UserOperationTypeGenerator(
                        getOperationTypesPool(),
                        getMinNumOfOperationTypesForAbnormalbehavior(),
                        getMaxNumOfOperationTypesForAbnormalbehavior());
        return generator;
    }

    private int getMinNumOfOperationTypesForAbnormalbehavior(){
        return 1;
    }

    private int getMaxNumOfOperationTypesForAbnormalbehavior(){
        return getOperationTypesPool().size();
    }

    @Override
    protected IStringGenerator getResultGenerator() {
        return new CustomStringGenerator(OPERATION_RESULT.FAILURE.value);
    }

    @Override
    protected double getPercentOfNormalUserPerDayOutOfTotalAmountOfUsers(){
        return PERCENT_OF_NORMAL_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS;
    }
    @Override
    protected double getNumOfEventsPerNormalUserPerHourOnAvg() {
        return NUM_OF_EVENTS_PER_NORMAL_USER_PER_HOUR_ON_AVG;
    }
    @Override
    protected double getPercentOfAdminUserPerDayOutOfTotalAmountOfUsers() {
        return PERCENT_OF_ADMIN_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS;
    }
    @Override
    protected double getNumOfEventsPerAdminUserPerHourOnAvg() {
        return NUM_OF_EVENTS_PER_ADMIN_USER_PER_HOUR_ON_AVG;
    }
    @Override
    protected double getPercentOfServiceAccountUserPerDayOutOfTotalAmountOfUsers() {
        return PERCENT_OF_SERVICE_ACCOUNT_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS;
    }
    @Override
    protected double getNumOfEventsPerServiceAccountUserPerHourOnAvg() {
        return NUM_OF_EVENTS_PER_SERVICE_ACCOUNT_USER_PER_HOUR_ON_AVG;
    }
    @Override
    protected double getPercentOfNormalUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return PERCENT_OF_NORMAL_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS;
    }
    @Override
    protected double getNumOfEventsPerNormalUserWithAnomaliesPerHourOnAvg() {
        return NUM_OF_EVENTS_PER_NORMAL_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG;
    }
    @Override
    protected double getPercentOfAdminUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return PERCENT_OF_ADMIN_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS;
    }
    @Override
    protected double getNumOfEventsPerAdminUserWithAnomaliesPerHourOnAvg() {
        return NUM_OF_EVENTS_PER_ADMIN_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG;
    }
    @Override
    protected double getPercentOfServiceAccountUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return PERCENT_OF_SERVICE_ACCOUNT_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS;
    }
    @Override
    protected double getNumOfEventsPerServiceAccountUserWithAnomaliesPerHourOnAvg() {
        return NUM_OF_EVENTS_PER_SERVICE_ACCOUNT_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG;
    }
}
