package com.rsa.netwitness.presidio.automation.common.scenarios.authentication;

import presidio.data.domain.User;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.authentication.AUTHENTICATION_OPERATION_TYPE;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationTypeCyclicGenerator;
import presidio.data.generators.common.*;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;
import presidio.data.generators.machine.FixedMachineGenerator;
import presidio.data.generators.machine.HostnameCustomListGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.SingleAdminUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by presidio on 8/15/17.
 */
public class AuthenticationOperationActions {

    public static List<AuthenticationEvent> getEventsByOperationName(String opName, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        switch (opName) {
            case "SuccessfulAuthenticationOperation":
                return getAuthenticationOperation(eventIdGen, timeGenerator, userGenerator);
            case "FailedAuthenticationOperation":
                return getFailedAuthenticationOperation(eventIdGen, timeGenerator, userGenerator);
            case "ProtectedAuthenticationOperation":
                return getProtectedAuthenticationOperation(eventIdGen, timeGenerator, userGenerator);
            case "DistinctDstDomainsAuthenticationOperation":
                return getDistinctMachinesAuthenticationOperation(eventIdGen, timeGenerator, userGenerator, false);
            case "DistinctSrcDomainsAuthenticationOperation":
                return getDistinctMachinesAuthenticationOperation(eventIdGen, timeGenerator, userGenerator, true);
            case "dstMachineNameRegexClusterInteractiveRemote":
                return getDstMachineNameRegexClusterInteractiveRemoteAuthenticationOperation(eventIdGen, timeGenerator, userGenerator);
            case "NullSrcMachineId":
                return getFailedAuthenticationOperationNullSrcMachineId(eventIdGen, timeGenerator, userGenerator);
            case "NullDstMachineId":
                return getFailedAuthenticationOperationNullDstMachineId(eventIdGen, timeGenerator, userGenerator);
            default:
                return null;
        }
    }

    /*********************************    Authentication operations:    *********************************/

    public static List<AuthenticationEvent> getAuthenticationOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        eventGenerator.setSrcMachineGenerator(new QuestADMachineGenerator());
        QuestADMachineGenerator destMachineGenerator = new QuestADMachineGenerator();
        destMachineGenerator.getNext();
        eventGenerator.setDstMachineGenerator(destMachineGenerator);
        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getFailedAuthenticationOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        eventGenerator.setResultGenerator(new CustomStringGenerator(OPERATION_RESULT.FAILURE.value));
        eventGenerator.setSrcMachineGenerator(new QuestADMachineGenerator());
        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        dstMachineGenerator.getNext();  // This is a trick to get dest machines different from src using same default generator
        eventGenerator.setDstMachineGenerator(dstMachineGenerator);

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getFailedAuthenticationOperationNullSrcMachineId(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        eventGenerator.setResultGenerator(new CustomStringGenerator(OPERATION_RESULT.FAILURE.value));

        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        IStringGenerator nullSrcMachineId = new HostnameCustomListGenerator(new String[] {null});
        srcMachineGenerator.setMachineIdGenerator(nullSrcMachineId);
        eventGenerator.setSrcMachineGenerator(srcMachineGenerator);

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getFailedAuthenticationOperationNullDstMachineId(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        eventGenerator.setResultGenerator(new CustomStringGenerator(OPERATION_RESULT.FAILURE.value));

        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        IStringGenerator nullDstMachineId = new HostnameCustomListGenerator(new String[] {null});
        dstMachineGenerator.setMachineIdGenerator(nullDstMachineId);
        eventGenerator.setDstMachineGenerator(dstMachineGenerator);

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getProtectedAuthenticationOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {"Protected", "None"}, new int[] {90,10});
        eventGenerator.setResultGenerator(opResultGenerator);
        eventGenerator.setSrcMachineGenerator(new QuestADMachineGenerator());
        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        dstMachineGenerator.getNext();  // This is a trick to get dest machines different from src using same default generator
        eventGenerator.setDstMachineGenerator(dstMachineGenerator);

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getDistinctMachinesAuthenticationOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator, boolean isSrcMachine) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);

        String[] distinctDomainsList = new String[100];
        String[] distinctMachinesIdList = new String[100];
        for (int i = 0; i < 100; i++) {
            distinctDomainsList[i] = ((isSrcMachine) ? "src" : "dst") + "domain_" + i;
            distinctMachinesIdList[i] = ((isSrcMachine) ? "src" : "dst") + "machine_" + i;

        }

        QuestADMachineGenerator machineGenerator = new QuestADMachineGenerator();
        machineGenerator.setMachineDomainGenerator(new StringCyclicValuesGenerator(distinctDomainsList));
        machineGenerator.setMachineIdGenerator(new StringCyclicValuesGenerator(distinctMachinesIdList));

        if (isSrcMachine) {
            eventGenerator.setSrcMachineGenerator(machineGenerator);
            eventGenerator.setDstMachineGenerator(new QuestADMachineGenerator());
        } else {
            eventGenerator.setSrcMachineGenerator(new QuestADMachineGenerator());
            eventGenerator.setDstMachineGenerator(machineGenerator);
        }

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getSameSrcDstMachinesAuthenticationOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);

        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        eventGenerator.setSrcMachineGenerator(srcMachineGenerator);
        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        eventGenerator.setDstMachineGenerator(dstMachineGenerator);

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getAbnormalSiteAuthenticationOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator, String [] sites) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        eventGenerator.setSiteGenerator(new StringCyclicValuesGenerator(sites));

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getNumberOfDistinctSrcMachineNameRegexClusterAuthenticationOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator, String [] srcMachine) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);

        AuthenticationOperationGenerator authenticationOperationGenerator = new AuthenticationOperationGenerator();
        IOperationTypeGenerator operationTypeCyclicGenerator = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.INTERACTIVE.value, Arrays.asList(new String[] {"INTERACTIVE"})));
        authenticationOperationGenerator.setOperationTypeGenerator(operationTypeCyclicGenerator);
        eventGenerator.setAuthenticationOperationGenerator(authenticationOperationGenerator);

        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        srcMachineGenerator.setMachineIdGenerator((new StringCyclicValuesGenerator(srcMachine)));
        eventGenerator.setSrcMachineGenerator(srcMachineGenerator);

        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        dstMachineGenerator.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[] {"dst_world"}));
        eventGenerator.setDstMachineGenerator(dstMachineGenerator);

        eventGenerator.setSiteGenerator(new StringCyclicValuesGenerator(new String[] {"site"}));

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getDstMachineNameRegexClusterInteractiveRemoteAuthenticationOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        AuthenticationOperationGenerator authenticationOperationGenerator = new AuthenticationOperationGenerator();
        IOperationTypeGenerator operationTypeCyclicGenerator = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.REMOTE_INTERACTIVE.value, Arrays.asList(new String[] {"INTERACTIVE_REMOTE"})));
        authenticationOperationGenerator.setOperationTypeGenerator(operationTypeCyclicGenerator);
        eventGenerator.setAuthenticationOperationGenerator(authenticationOperationGenerator);

        QuestADMachineGenerator machineGenerator = new QuestADMachineGenerator();
        machineGenerator.setMachineDomainGenerator(new StringRegexCyclicValuesGenerator("domain\\#[a-z]{1}[A-Z]{1}[1-9]{1}"));
        machineGenerator.setMachineIdGenerator(new StringRegexCyclicValuesGenerator("machine\\#[a-z]{1}[A-Z]{1}[1-9]{1}"));
        machineGenerator.setMachineNameRegexClusterGenerator(new StringRegexCyclicValuesGenerator("cluster\\#[a-z]{1}[A-Z]{1}[1-9]{1}"));

        eventGenerator.setDstMachineGenerator(machineGenerator);

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> getManyDistinctDstDomainsOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);

        String[] distinctDomainsList = new String[100];
        String[] distinctMachinesIdList = new String[100];
        for (int i = 0; i < 100; i++) {
            distinctDomainsList[i] = "somedomain_" + i;
            distinctMachinesIdList[i] = "machine_" + i;
        }

        eventGenerator.setSrcMachineGenerator(new QuestADMachineGenerator());

        QuestADMachineGenerator machineGenerator = new QuestADMachineGenerator();
        machineGenerator.setMachineDomainGenerator(new StringCyclicValuesGenerator(distinctDomainsList));
        machineGenerator.setMachineIdGenerator(new StringCyclicValuesGenerator(distinctMachinesIdList));

        eventGenerator.setDstMachineGenerator(machineGenerator);

        return eventGenerator.generate();
    }

    public static List<AuthenticationEvent> alertsSanityTestEvents(int historicalStartDay, int anomalyDay) throws GeneratorException {
        List<AuthenticationEvent> events = new ArrayList<>();

        final String testUser1 = "e2e_auth_user1";
        final String testUser2 = "e2e_auth_user2";
        final String testUser3 = "e2e_auth_user3";
        final String testCase = "e2e_auth";

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testCase);

        ITimeGenerator normalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), 30, historicalStartDay, anomalyDay);
        ITimeGenerator normalTimeGeneratorCont = new MinutesIncrementTimeGenerator(LocalTime.of(8, 1), LocalTime.of(16, 0), 60, historicalStartDay, anomalyDay);
        ITimeGenerator abnormalTimeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(2, 0), LocalTime.of(3, 0), 7, anomalyDay, anomalyDay - 1);
        ITimeGenerator abnormalTimeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(12, 0), LocalTime.of(13, 0), 1, anomalyDay, anomalyDay - 1);
        ITimeGenerator abnormalTimeGenerator3 = new MinutesIncrementTimeGenerator(LocalTime.of(10, 0), LocalTime.of(11, 0), 1, anomalyDay, anomalyDay - 1);

        SingleUserGenerator userGenerator1 = new SingleUserGenerator(testUser1);
        SingleUserGenerator userGenerator2 = new SingleUserGenerator(testUser2);
        IUserGenerator userGenerator3 = new SingleAdminUserGenerator(testUser3);

        /** High number of distinct destination Domains
         * One user
         * 1. normal - same domain in all events
         * 2. abnormal - 12 (1h * 6ev) different dstDomains
         * */
        events.addAll(AuthenticationOperationActions.getAuthenticationOperation(eventIdGen, normalTimeGenerator, userGenerator1));
        events.addAll(AuthenticationOperationActions.getManyDistinctDstDomainsOperation(eventIdGen, abnormalTimeGenerator1, userGenerator1));

        /** High number of failed authentications
         * One user
         * 1. normal - 2 per hour succeeded login, 1 failed login
         * 2. abnormal - 60 failed login operations
         * */
        events.addAll(AuthenticationOperationActions.getAuthenticationOperation(eventIdGen, normalTimeGenerator, userGenerator2));
        events.addAll(AuthenticationOperationActions.getFailedAuthenticationOperation(eventIdGen, normalTimeGeneratorCont, userGenerator2));
        events.addAll(AuthenticationOperationActions.getFailedAuthenticationOperation(eventIdGen, abnormalTimeGenerator2, userGenerator2));

        /** High number of successful authentications
         * One user
         * 1. normal - 2 per hour succeeded login, 1 failed login
         * 2. abnormal - 60 succeeded login operations
         * */
        events.addAll(AuthenticationOperationActions.getFailedAuthenticationOperation(eventIdGen, normalTimeGenerator, userGenerator3));
        events.addAll(AuthenticationOperationActions.getAuthenticationOperation(eventIdGen, normalTimeGeneratorCont, userGenerator3));
        events.addAll(AuthenticationOperationActions.getAuthenticationOperation(eventIdGen, abnormalTimeGenerator3, userGenerator3));

        /** High number of successful distinct source computers
         * One user
         * 1. normal - 2 per hour succeeded login, from 1-2 computers
         * 2. abnormal - 12 distinct computers
         * */


        return events;
    }

    public static List<AuthenticationEvent> getAllOperationTypes(String testUser) throws GeneratorException {
        /**
         * This method is for all operation types (event types) verification, not for anomaly
         */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser); // Not admin
        User user = userGenerator.getNext();
        FixedMachineGenerator srcMachineGenerator = new FixedMachineGenerator(user.getUserId() + "_SRC");
        FixedMachineGenerator dstMachineGenerator = new FixedMachineGenerator(user.getUserId() + "_DEST");

        List<OperationType> operationTypes = buildOperationTypesList();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator = new AuthenticationOperationTypeCyclicGenerator(operationTypes.toArray(new OperationType[0]));
        opTypeGenerator.setValuesList(operationTypes.toArray(new OperationType[0]));
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
        eventGenerator.setSrcMachineGenerator(srcMachineGenerator);
        eventGenerator.setDstMachineGenerator(dstMachineGenerator);
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        AuthenticationOperationGenerator authenticationOperationGenerator = new AuthenticationOperationGenerator();
        authenticationOperationGenerator.setOperationTypeGenerator(opTypeGenerator);
        eventGenerator.setAuthenticationOperationGenerator(authenticationOperationGenerator);

        List<AuthenticationEvent> events = eventGenerator.generate();

        return events;
    }

    private static List<OperationType> buildOperationTypesList() {
        List<OperationType> operationTypes = new ArrayList();
        operationTypes.add(new OperationType("INTERACTIVE"));
        operationTypes.add(new OperationType("NETWORK"));
        operationTypes.add(new OperationType("USER_FAILED_TO_LOG_ON_INTERACTIVELY"));
        operationTypes.add(new OperationType("REMOTE_INTERACTIVE"));
        operationTypes.add(new OperationType("USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER"));
        operationTypes.add(new OperationType("OTHER_OPERATION_SHOULD_NOT_BE_MAPPED"));
        return operationTypes;
    }

    protected static List<OperationType> buildSucceededOperationTypesList() {
        List<OperationType> operationTypes = new ArrayList();
        operationTypes.add(new OperationType("INTERACTIVE"));
        operationTypes.add(new OperationType("REMOTE_INTERACTIVE"));
        operationTypes.add(new OperationType("NETWORK"));
        return operationTypes;
    }
}
