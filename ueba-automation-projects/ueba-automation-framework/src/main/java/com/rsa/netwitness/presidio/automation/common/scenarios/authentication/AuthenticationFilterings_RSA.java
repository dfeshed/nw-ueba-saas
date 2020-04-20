package com.rsa.netwitness.presidio.automation.common.scenarios.authentication;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationTypeCyclicGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;
import presidio.data.generators.machine.HostnameCustomListGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationFilterings_RSA {

    /*********************************    Destination User@Domain - contains $    *******(*************************/

    public static List<AuthenticationEvent> getDestMachineNameWithDollar(String testUser, ITimeGenerator timeGenerator) throws GeneratorException {
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);

        HostnameCustomListGenerator machineIdGenerator = new HostnameCustomListGenerator(new String[] {"host_1$","host_2$","host_3$"});
        SimpleMachineGenerator machineGenerator = new SimpleMachineGenerator();
        machineGenerator.setMachineIdGenerator(machineIdGenerator);

        eventGenerator.setDstMachineGenerator(machineGenerator);
        return eventGenerator.generate();
    }

    /**************    Include only <Interactive, RemoteInteractive, CachedInteractive, Unlock>    ****************/

    public static List<AuthenticationEvent> getInteractiveOperations(String testUser, ITimeGenerator timeGenerator) throws GeneratorException {

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);

        AuthenticationOperationGenerator operationGenerator = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator = new AuthenticationOperationTypeCyclicGenerator(
                new OperationType[] {new OperationType("Interactive"), new OperationType("RemoteInteractive"),
                new OperationType("CachedInteractive"), new OperationType("Unlock"),
                new OperationType("Network"), new OperationType("someotheroperation")}
        );
        operationGenerator.setOperationTypeGenerator(opTypeGenerator);
        eventGenerator.setAuthenticationOperationGenerator(operationGenerator);
        return eventGenerator.generate();
    }

    /*********************************    Logon Types    *********************************/

    private static List<OperationType> buildOperationTypesList() {
        List<OperationType> operationTypes = new ArrayList();
        operationTypes.add(new OperationType("User failed to log on interactively"));
        operationTypes.add(new OperationType("User failed to log on interactively from a remote computer"));
        operationTypes.add(new OperationType("User failed to authenticate through Kerberos"));
        operationTypes.add(new OperationType("User logged on interactively"));
        operationTypes.add(new OperationType("User logged on interactively from a remote computer"));
        operationTypes.add(new OperationType("User authenticated through Kerberos"));
        return operationTypes;
    }

}
