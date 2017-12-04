package presidio.data.generators.event.authentication;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.authentication.AUTHENTICATION_OPERATION_TYPE;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationTypeCyclicGenerator;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.machine.QuestADMachineGenerator;

public class AuthenticationEventsGeneratorTemplateFactory {

    public AuthenticationEventsGenerator getFailedKerberosAuthenticationGenerator() throws GeneratorException {

        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();
        AuthenticationOperationGenerator opGenerator = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_FAILED_TO_AUTHENTICATE_THROUGH_KERBEROS.value));
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        generator.setAuthenticationOperationGenerator(opGenerator);
        StringCyclicValuesGenerator resultGenerator = new StringCyclicValuesGenerator(OPERATION_RESULT.FAILURE.value);
        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        generator.setSrcMachineGenerator(srcMachineGenerator);
        generator.setDstMachineGenerator(dstMachineGenerator);
        generator.setResultGenerator(resultGenerator);

        return generator;
    }

    public AuthenticationEventsGenerator getSuccessRemoteLogonAuthenticationGenerator() throws  GeneratorException {
        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();
        AuthenticationOperationGenerator opGenerator = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_LOGGED_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER.value));
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        generator.setAuthenticationOperationGenerator(opGenerator);
        StringCyclicValuesGenerator resultGenerator = new StringCyclicValuesGenerator(OPERATION_RESULT.SUCCESS.value);
        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        generator.setSrcMachineGenerator(srcMachineGenerator);
        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        dstMachineGenerator.setMachineIPGenerator(new FixedIPsGenerator(new String[] {"192.168.0.3"}));
        generator.setDstMachineGenerator(dstMachineGenerator);
        generator.setResultGenerator(resultGenerator);

        return generator;
    }

    public AuthenticationEventsGenerator getSuccessLogonAuthenticationGenerator() throws GeneratorException {
        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();
        AuthenticationOperationGenerator opGenerator = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_LOGGED_ON_INTERACTIVELY.value));
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        generator.setAuthenticationOperationGenerator(opGenerator);        StringCyclicValuesGenerator resultGenerator = new StringCyclicValuesGenerator(OPERATION_RESULT.SUCCESS.value);
        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        generator.setSrcMachineGenerator(srcMachineGenerator);
        generator.setDstMachineGenerator(dstMachineGenerator);
        generator.setResultGenerator(resultGenerator);

        return generator;
    }
}