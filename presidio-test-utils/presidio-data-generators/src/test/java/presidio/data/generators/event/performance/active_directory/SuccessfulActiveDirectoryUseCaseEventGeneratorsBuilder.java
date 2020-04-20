package presidio.data.generators.event.performance.active_directory;

import presidio.data.domain.event.OperationType;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;


public class SuccessfulActiveDirectoryUseCaseEventGeneratorsBuilder extends ActiveDirectoryUseCaseEventGeneratorsBuilder{

    private static final double PERCENT_OF_NORMAL_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.75d;
    private static final int NUM_OF_EVENTS_PER_NORMAL_USER_PER_HOUR_ON_AVG = 2;

    private static final double PERCENT_OF_ADMIN_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.99d;
    private static final int NUM_OF_EVENTS_PER_ADMIN_USER_PER_HOUR_ON_AVG = 20;

    private static final double PERCENT_OF_SERVICE_ACCOUNT_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 1d;
    private static final int NUM_OF_EVENTS_PER_SERVICE_ACCOUNT_USER_PER_HOUR_ON_AVG = 5;

    private static final double PERCENT_OF_NORMAL_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.002d;
    private static final int NUM_OF_EVENTS_PER_NORMAL_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 80;

    private static final double PERCENT_OF_ADMIN_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.002d;
    private static final int NUM_OF_EVENTS_PER_ADMIN_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 800;

    private static final double PERCENT_OF_SERVICE_ACCOUNT_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.002d;
    private static final int NUM_OF_EVENTS_PER_SERVICE_ACCOUNT_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 200;

    private List<OperationType> operationTypesPool;

    public SuccessfulActiveDirectoryUseCaseEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                                               List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                                               List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                                               IUserGenerator adminUserGenerator,
                                                               List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                                               List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                                               IUserGenerator serviceAccountUserGenerator,
                                                               List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange) throws GeneratorException {
        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange, adminUserGenerator,
                adminUserActivityRange, adminUserAbnormalActivityRange, serviceAccountUserGenerator,
                serviceAcountUserActivityRange);
    }

    private List<OperationType> getOperationTypesPool(){
        if(operationTypesPool == null){
            operationTypesPool = getAllOperationTypes();
        }
        return operationTypesPool;
    }

    @Override
    protected String getUseCaseTestName() {
        return "SuccessfulActiveDirectoryEvent";
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
