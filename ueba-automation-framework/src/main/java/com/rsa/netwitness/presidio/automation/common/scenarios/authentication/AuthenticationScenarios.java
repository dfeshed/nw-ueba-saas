package com.rsa.netwitness.presidio.automation.common.scenarios.authentication;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationTypeCyclicGenerator;
import presidio.data.generators.common.CustomStringGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;
import presidio.data.generators.machine.HostnameCustomListGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationScenarios {

    public static List<AuthenticationEvent> getBruteForceScenarioOnLinux(String testUser, int anomalyDay) throws GeneratorException {
        ITimeGenerator janesTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(5, 30), LocalTime.of(15, 30), 60, anomalyDay + 28, anomalyDay-1);
        ITimeGenerator hackTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(5, 00), LocalTime.of(8, 00), 1, anomalyDay, anomalyDay - 1);
        ITimeGenerator breachTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(9, 00), 10, anomalyDay, anomalyDay - 1);
        return AuthenticationScenarios.getBruteForceCustomTimeScenarioOnLinux(testUser, janesTimeGenerator, hackTimeGenerator, breachTimeGenerator);
    }

    public static List<AuthenticationEvent> getBruteForceCustomTimeScenarioOnLinux(String testUser, ITimeGenerator regularTimeGen, ITimeGenerator bruteForceAttackTimeGen, ITimeGenerator breachTimeGen) throws GeneratorException {

        /** Jane S. usually works:
         *          from 8:30 to 15:30
         *          on one source machine - rhlinux
         *          successfully logs in via sshd, once per hour
         *          in 5% of cases fails to log in
         *
         * Anomaly: multiple failed authentications
         *          at unusual time 2:00 - 6:00, every minute
         *          from 5 different machines
         *          at 6:00 - multiple successful authentications from 2 of 5 abnormal machines
         * **/
        List<AuthenticationEvent> events = new ArrayList<>();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        /** Normal **/
        AuthenticationEventsGenerator janesEventGenerator = new AuthenticationEventsGenerator();
        janesEventGenerator.setUserGenerator(userGenerator);
        janesEventGenerator.setTimeGenerator(regularTimeGen);
        janesEventGenerator.setEventIDGenerator(eventIdGen);
        // OS - rhlinux
        IStringGenerator linuxOsGen = new CustomStringGenerator("rhlinux");

        // event data source = action: /usr/sbin/sshd
        IStringGenerator dataSourceGenerator = new CustomStringGenerator("/usr/sbin/sshd");
        janesEventGenerator.setDataSourceGenerator(dataSourceGenerator);

        // Hacked user machine
        SimpleMachineGenerator machineGenerator = new SimpleMachineGenerator();
        IStringGenerator machineIdGen = new CustomStringGenerator(testUser.replaceAll("\\s+","").replaceAll("\\.","").toLowerCase() + "_rhel");
        machineGenerator.setMachineIdGenerator(machineIdGen);
        machineGenerator.setOsVersionGenerator(linuxOsGen);

        janesEventGenerator.setSrcMachineGenerator(machineGenerator);

        // operation type: user_auth // "USER_LOGIN|CRED_ACQ|USER_AUTH"
        AuthenticationOperationGenerator opGenerator = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator = new AuthenticationOperationTypeCyclicGenerator(new OperationType("USER_AUTH", new ArrayList<>()));
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        janesEventGenerator.setAuthenticationOperationGenerator(opGenerator);

        // successful most of times
        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {"success", "failure"}, new int[] {93,7});
        janesEventGenerator.setResultGenerator(opResultGenerator);
        events.addAll(janesEventGenerator.generate());

        /** Abormal
         *  Machines runner01 - runner05 will to find out the pwd
         *  fail to connect multiple times using Jane's user during abnormal hours
         *
         *  To improve scenario - Jane's user probably will be locked after several failed attempts.
         *  Need another persona / script that will unlock her.
         *  **/
        AuthenticationEventsGenerator hackEventsGenerator = new AuthenticationEventsGenerator();
        hackEventsGenerator.setUserGenerator(userGenerator);
        hackEventsGenerator.setTimeGenerator(bruteForceAttackTimeGen);
        hackEventsGenerator.setEventIDGenerator(eventIdGen);

        hackEventsGenerator.setDataSourceGenerator(dataSourceGenerator);

        SimpleMachineGenerator hackerMachinesGenerator = new SimpleMachineGenerator();
        // Machines comp_xx
        IStringGenerator hackerMachineIdGen = new HostnameCustomListGenerator(new String[] {"comp_lucky","comp_lucky_a","comp_lucky_b","comp_sl","comp_sl_a","123:123"});
        hackerMachinesGenerator.setMachineIdGenerator(hackerMachineIdGen);
        hackerMachinesGenerator.setOsVersionGenerator(linuxOsGen);

        hackEventsGenerator.setSrcMachineGenerator(hackerMachinesGenerator);
        hackEventsGenerator.setAuthenticationOperationGenerator(opGenerator);
        // fail in 99% of times - trying to find out Jane's password
        OperationResultPercentageGenerator failedOpResultGenerator = new OperationResultPercentageGenerator(new String[] {"success", "failure"}, new int[] {1,99});
        hackEventsGenerator.setResultGenerator(failedOpResultGenerator);

        events.addAll(hackEventsGenerator.generate());

        /** Machines runner04,runner05 where lucky to find out the pwd
         *  and now connect successfully multiple times during one hour
        **/
        AuthenticationEventsGenerator breachEventsGenerator = new AuthenticationEventsGenerator();
        breachEventsGenerator.setUserGenerator(userGenerator);
        breachEventsGenerator.setTimeGenerator(breachTimeGen);
        breachEventsGenerator.setEventIDGenerator(eventIdGen);

        breachEventsGenerator.setDataSourceGenerator(dataSourceGenerator);

        SimpleMachineGenerator luckyHackerMachinesGenerator = new SimpleMachineGenerator();
        IStringGenerator luckyHackerMachineIdGen= new HostnameCustomListGenerator(new String[] {"comp_lucky","comp_sl"});
        luckyHackerMachinesGenerator.setMachineIdGenerator(luckyHackerMachineIdGen);

        // OS - rhlinux
        luckyHackerMachinesGenerator.setOsVersionGenerator(linuxOsGen);
        breachEventsGenerator.setSrcMachineGenerator(luckyHackerMachinesGenerator);
        breachEventsGenerator.setAuthenticationOperationGenerator(opGenerator);

        // successfully works using Jane's credentials
        OperationResultPercentageGenerator successOpGenerator = new OperationResultPercentageGenerator(new String[] {"success"}, new int[] {100});
        breachEventsGenerator.setResultGenerator(successOpGenerator);

        events.addAll(breachEventsGenerator.generate());

        return events;
    }
}
