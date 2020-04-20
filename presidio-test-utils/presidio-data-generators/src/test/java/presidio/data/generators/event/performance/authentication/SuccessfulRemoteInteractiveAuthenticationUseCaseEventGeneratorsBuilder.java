package presidio.data.generators.event.performance.authentication;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.authentication.AUTHENTICATION_OPERATION_TYPE;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationTypeCyclicGenerator;
import presidio.data.generators.authenticationop.IAuthenticationOperationGenerator;
import presidio.data.generators.common.CustomStringGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.Arrays;
import java.util.List;

public class SuccessfulRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder extends AuthenticationUseCaseEventGeneratorsBuilder{
    private static final double PERCENT_OF_NORMAL_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.7d;
    private static final int NUM_OF_EVENTS_PER_NORMAL_USER_PER_HOUR_ON_AVG = 2;

    private static final double PERCENT_OF_ADMIN_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.99d;
    private static final int NUM_OF_EVENTS_PER_ADMIN_USER_PER_HOUR_ON_AVG = 20;

    private static final double PERCENT_OF_SERVICE_ACCOUNT_USER_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 1d;
    private static final int NUM_OF_EVENTS_PER_SERVICE_ACCOUNT_USER_PER_HOUR_ON_AVG = 5;

    private static final double PERCENT_OF_NORMAL_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.002d;
    private static final int NUM_OF_EVENTS_PER_NORMAL_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 8;

    private static final double PERCENT_OF_ADMIN_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.002d;
    private static final int NUM_OF_EVENTS_PER_ADMIN_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 80;

    private static final double PERCENT_OF_SERVICE_ACCOUNT_USER_WITH_ANOMALIES_PER_DAY_OUT_OF_TOTAL_AMOUNT_OF_USERS = 0.002d;
    private static final int NUM_OF_EVENTS_PER_SERVICE_ACCOUNT_USER_WITH_ANOMALIES_PER_HOUR_ON_AVG = 20;

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




    public SuccessfulRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder
            (IUserGenerator normalUserGenerator,
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
             IMachineGenerator normalUserDstMachinesGenerator,
             IMachineGenerator normalUserAbnormalDstMachinesGenerator,
             IMachineGenerator adminUserDstMachinesGenerator,
             IMachineGenerator adminUserAbnormalDstMachinesGenerator,
             IMachineGenerator serviceAccountUserDstMachinesGenerator,
             IMachineGenerator serviceAccountUserAbnormalDstMachinesGenerator) throws GeneratorException {
        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange,
                adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange,
                serviceAccountUserGenerator, serviceAcountUserActivityRange, normalUserSrcMachinesGenerator,
                normalUserAbnormalSrcMachinesGenerator, adminUserSrcMachinesGenerator,
                adminUserAbnormalSrcMachinesGenerator, serviceAccountUserSrcMachinesGenerator,
                serviceAccountUserAbnormalSrcMachinesGenerator, normalUserDstMachinesGenerator,
                normalUserAbnormalDstMachinesGenerator, adminUserDstMachinesGenerator,
                adminUserAbnormalDstMachinesGenerator, serviceAccountUserDstMachinesGenerator,
                serviceAccountUserAbnormalDstMachinesGenerator);
    }

    protected IAuthenticationOperationGenerator getOperationGenerator(){
        AuthenticationOperationGenerator operationGenerator = new AuthenticationOperationGenerator();
        operationGenerator.setOperationTypeGenerator(new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.REMOTE_INTERACTIVE.value, Arrays.asList(new String[] {"REMOTE_INTERACTIVE"}))));
        return operationGenerator;
    }

    protected IStringGenerator getResultGenerator(){
        return new CustomStringGenerator(OPERATION_RESULT.SUCCESS.value);
    }





    @Override
    protected String getUseCaseTestName() {
        return "successfulRemoteInteractiveAuthentication";
    }
}
