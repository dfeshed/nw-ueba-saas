package presidio.data.generators.event.authentication;

import presidio.data.generators.authenticationop.AuthenticationTypeCyclicGenerator;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.machine.QuestADMachineGenerator;

public class AuthenticationEventsGeneratorTemplateFactory {

    public AuthenticationEventsGenerator getFailedKerberosAuthenticationGenerator() throws GeneratorException {

        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();

        AuthenticationTypeCyclicGenerator operationTypeGenerator = new AuthenticationTypeCyclicGenerator("User failed to authenticate through Kerberos");
        generator.setOperationTypeGenerator(operationTypeGenerator);
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

        AuthenticationTypeCyclicGenerator operationTypeGenerator = new AuthenticationTypeCyclicGenerator("User logged on interactively from a remote computer");
        generator.setOperationTypeGenerator(operationTypeGenerator);
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

        AuthenticationTypeCyclicGenerator operationTypeGenerator = new AuthenticationTypeCyclicGenerator("User logged on interactively");
        generator.setOperationTypeGenerator(operationTypeGenerator);
        StringCyclicValuesGenerator resultGenerator = new StringCyclicValuesGenerator(OPERATION_RESULT.SUCCESS.value);
        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        generator.setSrcMachineGenerator(srcMachineGenerator);
        generator.setDstMachineGenerator(dstMachineGenerator);

        generator.setResultGenerator(resultGenerator);

        return generator;
    }


}
