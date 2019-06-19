package presidio.data.generators.event.authentication;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.authentication.AUTHENTICATION_OPERATION_TYPE;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationTypeCyclicGenerator;
import presidio.data.generators.authenticationop.IAuthenticationOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.Arrays;
import java.util.List;

public class SuccessfulRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder extends AuthenticationUseCaseEventGeneratorsBuilder{




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
        try {
            return new OperationResultPercentageGenerator();
        } catch (GeneratorException e) {}
        return null;//should not happen. TODO: Add unit test.
    }

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
        return 0.49;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForNormalUsers() {
        return 120000;
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
        return 0.3;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForAdminUsers() {
        return 120000;
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
        return 0.0025;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForServiceAccountUsers() {
        return 120000;
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
    protected int getNumOfAdminUsersDailyForNonActiveWorkingHours() {
        return 10;
    }

    @Override
    protected double getEventProbabilityForAdminUsersForNonActiveWorkingHours() {
        return 0.001;
    }

    @Override
    protected int getNumOfServiceAccountUsersDailyForNonActiveWorkingHours() {
        return 2;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsersForNonActiveWorkingHours() {
        return 0.0005;
    }



    @Override
    protected String getUseCaseTestName() {
        return "successfulAuthentication";
    }
}
