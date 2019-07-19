package com.rsa.netwitness.presidio.automation.common.scenarios.authentication;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.authentication.AUTHENTICATION_OPERATION_TYPE;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationTypeCyclicGenerator;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;
import presidio.data.generators.user.SingleUserGenerator;
import com.rsa.netwitness.presidio.automation.common.scenarios.TimeScenarioTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class AuthenticationMachineAnomalies {

    public static List<AuthenticationEvent> getAbnormalSrcMachineActivity(String testUser, int anomalyDay) throws GeneratorException {
        return getAbnormalMachineActivity(true, testUser, anomalyDay);
    }
    public static List<AuthenticationEvent> getAbnormalDstMachineActivity(String testUser, int anomalyDay) throws GeneratorException {
        return getAbnormalMachineActivity(false, testUser, anomalyDay);
    }


    public static List<AuthenticationEvent> getSequenceOfAbnormalMachineActivity(Boolean isSrcMachine, String testUser, int anomalyDay) throws GeneratorException {
        // test anomaly sequencing:
        // normal behavior - user connects to 2 different machines per day during 5 days
        // sequence: anomaly1 - user connects to 10 machines on anomaly day
        // sequence: anomaly2 - user connects to the same 10 machines next day also
        // Expecting - high score for both anomaly days

        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        // GENERATOR1 - 5 days, testUser, connects to 2 normal machines named by user
        ITimeGenerator normalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(11, 59), 10, anomalyDay+12, anomalyDay+1);
        AuthenticationEventsGenerator eventGeneratorMachine1 =
                getScenarioGeneratorTemplate(isSrcMachine, normalTimeGenerator, eventIdGen, testUser, testUser + "_src_usual_machine1");

        AuthenticationEventsGenerator eventGeneratorMachine2 =
                getScenarioGeneratorTemplate(isSrcMachine, normalTimeGenerator, eventIdGen, testUser, testUser + "_src_usual_machine2");

        // GENERATOR2 - events and normal machines for OTHER USERS
        AuthenticationEventsGenerator otherUserEventGenerator =
                getScenarioGeneratorTemplate(isSrcMachine, normalTimeGenerator, eventIdGen, testUser + "_other", testUser + "_src_other_user_machine");

        // Generate events
        List<AuthenticationEvent> events = eventGeneratorMachine1.generate();
        events.addAll(eventGeneratorMachine2.generate());
        events.addAll(otherUserEventGenerator.generate());

        // GENERATOR3 and GENERATOR4 - sequence of anomalies
        // Anomaly 1 - testUser connected to 10 new src machine, low score expected
        ITimeGenerator anomalyTimeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(11, 59), 10, anomalyDay+1, anomalyDay);

        // Anomaly 2 - user connects to the same 10 machines next day also
        ITimeGenerator anomalyTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(11, 59), 10, anomalyDay, anomalyDay-1);

        for (int i = 1; i<=10; i++) {
            AuthenticationEventsGenerator anomaly1UserEventGenerator =
                    getScenarioGeneratorTemplate(isSrcMachine, anomalyTimeGenerator1, eventIdGen, testUser, testUser + "_src_new_machine" + i);
                events.addAll(anomaly1UserEventGenerator.generate());

            AuthenticationEventsGenerator anomaly2UserEventGenerator =
                    getScenarioGeneratorTemplate(isSrcMachine, anomalyTimeGenerator2, eventIdGen, testUser, testUser + "_src_new_machine" + i);
            events.addAll(anomaly2UserEventGenerator.generate());
        }

        return events;
    }

    public static List<AuthenticationEvent> getAbnormalMachineActivity(Boolean isSrcMachine, String testUser, int anomalyDay) throws GeneratorException {

        String srcOrDstString = isSrcMachine?"src":"dst";

        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        // GENERATOR1 - Normal time, testUser, normal machine named by user
        AuthenticationEventsGenerator eventGenerator =
                getScenarioGeneratorTemplate(isSrcMachine, TimeScenarioTemplate.getNormalTimeGenerator(), eventIdGen, testUser, testUser + "_" + srcOrDstString + "_usual_machine");
        setSucceedOperationsGenerator(eventGenerator);


        // GENERATOR2 - events and normal machines for OTHER USERS
        AuthenticationEventsGenerator otherUserEventGenerator =
                getScenarioGeneratorTemplate(isSrcMachine, TimeScenarioTemplate.getNormalTimeGenerator(), eventIdGen, testUser + "_other", testUser + "_" + srcOrDstString + "_other_user_machine");
        setSucceedOperationsGenerator(otherUserEventGenerator);

        // GENERATOR3
        // Anomaly 1 - testUser connected to new src machine, low score expected
        ITimeGenerator anomalyTimeGenerator1 =
        new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(11, 59), 30, anomalyDay, anomalyDay - 1);
        AuthenticationEventsGenerator anomaly1UserEventGenerator =
                getScenarioGeneratorTemplate(isSrcMachine, anomalyTimeGenerator1, eventIdGen, testUser, testUser + "_" + srcOrDstString + "_new_machine");
        setSucceedOperationsGenerator(anomaly1UserEventGenerator);

        // GENERATOR4
        // Anomaly 2 - testUser connected to src machine used by OTHER USERS, high score expected
        ITimeGenerator anomalyTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 00), LocalTime.of(14, 59), 30, anomalyDay, anomalyDay - 1);
        AuthenticationEventsGenerator anomaly2UserEventGenerator =
                getScenarioGeneratorTemplate(isSrcMachine, anomalyTimeGenerator2, eventIdGen, testUser, testUser + "_" + srcOrDstString + "_other_user_machine");
        setSucceedOperationsGenerator(anomaly2UserEventGenerator);

        // Generate events
        List<AuthenticationEvent> events = eventGenerator.generate();
        events.addAll(otherUserEventGenerator.generate());
        events.addAll(anomaly1UserEventGenerator.generate());
        events.addAll(anomaly2UserEventGenerator.generate());

        return events;
    }

    private static void setSucceedOperationsGenerator(AuthenticationEventsGenerator eventGenerator) {
        List<OperationType> operationTypes = AuthenticationOperationActions.buildSucceededOperationTypesList();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator = new AuthenticationOperationTypeCyclicGenerator(operationTypes.toArray(new OperationType[0]));
        AuthenticationOperationGenerator opGenerator = new AuthenticationOperationGenerator();
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        eventGenerator.setAuthenticationOperationGenerator(opGenerator);
    }

    public static List<AuthenticationEvent> getAbnormalMachineActivity(Boolean isSrcMachine, String testUser, ITimeGenerator normalTimeGenerator, ITimeGenerator abnormalTimeGenerator) throws GeneratorException {

        String srcOrDstString = isSrcMachine?"src":"dst";

        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        // GENERATOR1 - Normal time, testUser, normal machine named by user
        normalTimeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator =
                getScenarioGeneratorTemplate(isSrcMachine, normalTimeGenerator, eventIdGen, testUser, testUser + "_" + srcOrDstString + "_usual_machine");

        // GENERATOR2 - events and normal machines for OTHER USERS
        normalTimeGenerator.reset();
        AuthenticationEventsGenerator otherUserEventGenerator =
                getScenarioGeneratorTemplate(isSrcMachine, normalTimeGenerator, eventIdGen, testUser + "_other", testUser + "_" + srcOrDstString + "_other_user_machine");

        // Anomaly  - testUser connected to src machine used by OTHER USERS, high score expected
        abnormalTimeGenerator.reset();
        AuthenticationEventsGenerator anomaly2UserEventGenerator =
                getScenarioGeneratorTemplate(isSrcMachine, abnormalTimeGenerator, eventIdGen, testUser, testUser + "_" + srcOrDstString + "_other_user_machine");

        // Generate events
        List<AuthenticationEvent> events = eventGenerator.generate();
        events.addAll(otherUserEventGenerator.generate());
        events.addAll(anomaly2UserEventGenerator.generate());

        return events;
    }

    private static AuthenticationEventsGenerator getScenarioGeneratorTemplate(Boolean isSrcMachine, ITimeGenerator timeGenerator, EntityEventIDFixedPrefixGenerator eventIdGen, String userName, String machineName) throws GeneratorException {
        timeGenerator.reset();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();

        SingleUserGenerator userGenerator = new SingleUserGenerator(userName);
        eventGenerator.setUserGenerator(userGenerator);

        QuestADMachineGenerator machineGenerator = new QuestADMachineGenerator();
        machineGenerator.setMachineDomainGenerator(new StringCyclicValuesGenerator(new String[] {"domain_" + machineName}));
        machineGenerator.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[] {machineName}));

        if (isSrcMachine) {
            eventGenerator.setSrcMachineGenerator(machineGenerator);
            eventGenerator.setDstMachineGenerator(new QuestADMachineGenerator());
        } else {
            eventGenerator.setSrcMachineGenerator(new QuestADMachineGenerator());
            eventGenerator.setDstMachineGenerator(machineGenerator);
        }

        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIDGenerator(eventIdGen);
        return eventGenerator;
    }

    public static List<AuthenticationEvent> sumOfHighestDstMachineNameRegexClusterScoresUserIdInteractiveRemote(String testUser, ITimeGenerator normalTimeGenerator, ITimeGenerator abnormalTimeGenerator) throws GeneratorException {
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, normalTimeGenerator, userGenerator));
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("dstMachineNameRegexClusterInteractiveRemote", eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }


    public static List<AuthenticationEvent> getAbnormalSrcAndDstMachines_emptyModel_onlyIpinNoramlbehvor(String testUser, int anomalyDay) throws GeneratorException {
        List<Pair<String,String>> pairs = new ArrayList<>();

        pairs.add(Pair.of("host1", "203.104.0.35"));

        AuthenticationEventsGenerator events = new AuthenticationEventsGenerator();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        ITimeGenerator normalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(15, 00), 30, anomalyDay + 13, anomalyDay + 1);

        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        srcMachineGenerator.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"192.168.0.1"}));
        srcMachineGenerator.setMachineIPGenerator(new StringCyclicValuesGenerator(new String[]{"192.168.0.1"}));

        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        dstMachineGenerator.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"10.0.0.15"}));
        dstMachineGenerator.setMachineIPGenerator(new StringCyclicValuesGenerator(new String[]{"10.0.0.15"}));

        events.setEventIDGenerator(eventIdGen);
        events.setUserGenerator(userGenerator);
        events.setTimeGenerator(normalTimeGenerator);
        events.setSrcMachineGenerator(srcMachineGenerator);
        events.setDstMachineGenerator(dstMachineGenerator);
        List<AuthenticationEvent> eventList = events.generate();

        SingleUserGenerator anomalyUserGenerator = new SingleUserGenerator(testUser);
        ITimeGenerator anomalyTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(19, 00), LocalTime.of(21, 00), 1, anomalyDay + 1, anomalyDay - 1);

        QuestADMachineGenerator srcMachineGeneratorAnomaly = new QuestADMachineGenerator();
        srcMachineGeneratorAnomaly.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"host1", "host2"}));
        srcMachineGeneratorAnomaly.setMachineIPGenerator(new FixedIPsGenerator(new String[]{"host1", "host2"}));

        QuestADMachineGenerator dstMachineGeneratorAnomaly = new QuestADMachineGenerator();
        dstMachineGeneratorAnomaly.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"host3", "host4"}));
        dstMachineGeneratorAnomaly.setMachineIPGenerator(new FixedIPsGenerator(new String[]{"host3", "host4"}));

        events.setUserGenerator(anomalyUserGenerator);
        events.setTimeGenerator(anomalyTimeGenerator);
        events.setSrcMachineGenerator(srcMachineGeneratorAnomaly);
        events.setDstMachineGenerator(dstMachineGeneratorAnomaly);
        eventList.addAll(events.generate());

        return  eventList;
    }

    // In progress
    public static List<AuthenticationEvent> getAbnormalSrcAndDstMachines_includesSrcAndDestIPs(String testUser, int anomalyDay) throws GeneratorException {
        List<Pair<String,String>> pairs = new ArrayList<>();

        pairs.add(Pair.of("host1", "203.104.0.35"));

        AuthenticationEventsGenerator events = new AuthenticationEventsGenerator();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        ITimeGenerator normalTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(15, 00), 30, anomalyDay + 13, anomalyDay + 1);

        QuestADMachineGenerator srcMachineGenerator = new QuestADMachineGenerator();
        srcMachineGenerator.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"host111","host1111" , "source22"}));
        srcMachineGenerator.setMachineIPGenerator(new FixedIPsGenerator(new String[]{"host111","host1111" , "source22"}));

        QuestADMachineGenerator dstMachineGenerator = new QuestADMachineGenerator();
        dstMachineGenerator.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"host333", "host3333","destination22"}));
        dstMachineGenerator.setMachineIPGenerator(new FixedIPsGenerator(new String[]{"host3333", "host3333", "destination22"}));

        events.setEventIDGenerator(eventIdGen);
        events.setUserGenerator(userGenerator);
        events.setTimeGenerator(normalTimeGenerator);
        events.setSrcMachineGenerator(srcMachineGenerator);
        events.setDstMachineGenerator(dstMachineGenerator);
        List<AuthenticationEvent> eventList = events.generate();

        SingleUserGenerator anomalyUserGenerator = new SingleUserGenerator(testUser);
        ITimeGenerator anomalyTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(18, 00), LocalTime.of(20, 00), 10, anomalyDay + 1, anomalyDay - 1);

        QuestADMachineGenerator srcMachineGeneratorAnomaly = new QuestADMachineGenerator();
        srcMachineGeneratorAnomaly.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"192.168.0.1"}));
        srcMachineGeneratorAnomaly.setMachineIPGenerator(new FixedIPsGenerator(new String[]{"192.168.0.1"}));

        QuestADMachineGenerator dstMachineGeneratorAnomaly = new QuestADMachineGenerator();
        dstMachineGeneratorAnomaly.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"10.0.0.15"}));
        dstMachineGeneratorAnomaly.setMachineIPGenerator(new FixedIPsGenerator(new String[]{"10.0.0.15"}));

        events.setUserGenerator(anomalyUserGenerator);
        events.setTimeGenerator(anomalyTimeGenerator);
        events.setSrcMachineGenerator(srcMachineGeneratorAnomaly);
        events.setDstMachineGenerator(dstMachineGeneratorAnomaly);
        eventList.addAll(events.generate());

        SingleUserGenerator anomalyHost_UserGenerator = new SingleUserGenerator(testUser);
        ITimeGenerator anomalyTimeHostGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(20, 00), LocalTime.of(22, 00), 1, anomalyDay + 1, anomalyDay - 1);

        QuestADMachineGenerator srcMachineGeneratorHostAnomaly = new QuestADMachineGenerator();
        srcMachineGeneratorHostAnomaly.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"Anomaly_a123456host", "Anomaly_b123456host23","Anomaly_c123456host", "Anomaly_d123456host23","Anomaly_e123456host", "Anomaly_f123456host23","Anomaly_g123456host", "Anomaly_h123456host23","Anomaly_i123456host", "Anomaly_j123456host23","Anomaly_k123456host", "Anomaly_l123456host23"}));
        srcMachineGeneratorHostAnomaly.setMachineIPGenerator(new FixedIPsGenerator(new String[]{"Anomaly_a123456host", "Anomaly_b123456host23","Anomaly_c123456host", "Anomaly_d123456host23","Anomaly_e123456host", "Anomaly_f123456host23","Anomaly_g123456host", "Anomaly_h123456host23","Anomaly_i123456host", "Anomaly_j123456host23","Anomaly_k123456host", "Anomaly_l123456host23"}));

        QuestADMachineGenerator dstMachineGeneratorHostAnomaly = new QuestADMachineGenerator();
        dstMachineGeneratorHostAnomaly.setMachineIdGenerator(new StringCyclicValuesGenerator(new String[]{"AnomalyDst123456host", "AnomalyDst123456host123"}));
        dstMachineGeneratorHostAnomaly.setMachineIPGenerator(new FixedIPsGenerator(new String[]{"AnomalyDst123456host", "AnomalyDst123456host123"}));

        events.setUserGenerator(anomalyHost_UserGenerator);
        events.setTimeGenerator(anomalyTimeHostGenerator);
        events.setSrcMachineGenerator(srcMachineGeneratorHostAnomaly);
        events.setDstMachineGenerator(srcMachineGeneratorHostAnomaly);
        eventList.addAll(events.generate());
        return eventList;
    }

    public static List<AuthenticationEvent> getAbnormalOperationType(Boolean isSrcMachine, String testUser, int anomalyDay) throws GeneratorException {

        // create events id generator, use it in all event generators to ensure unique event id
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);

        String srcOrDstString = isSrcMachine?"src":"dst";
        // GENERATOR1 -
        ITimeGenerator TimeHostGenerator_a =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(16, 30), 10, anomalyDay + 13, anomalyDay + 2);
        AuthenticationEventsGenerator eventGenerator_a =
                getScenarioGeneratorTemplate(isSrcMachine, TimeHostGenerator_a, eventIdGen, testUser, testUser );

        AuthenticationOperationGenerator opGenerator = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.REMOTE_INTERACTIVE.value));
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        eventGenerator_a.setAuthenticationOperationGenerator(opGenerator);

        // GENERATOR2 -
        ITimeGenerator TimeHostGenerator_b =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(11, 30), 5, anomalyDay + 2, anomalyDay - 1);
        AuthenticationEventsGenerator eventGenerator_b =
                getScenarioGeneratorTemplate(isSrcMachine, TimeHostGenerator_b, eventIdGen, testUser + "_other", testUser + "_" + srcOrDstString + "_other_user_machine");

        AuthenticationOperationGenerator opGenerator_b = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator_b = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_FAILED_TO_AUTHENTICATE_THROUGH_KERBEROS.value));
        opGenerator.setOperationTypeGenerator(opTypeGenerator_b);
        eventGenerator_b.setAuthenticationOperationGenerator(opGenerator_b);

        // GENERATOR3
        ITimeGenerator TimeHostGenerator_c =
                new MinutesIncrementTimeGenerator(LocalTime.of(12, 00), LocalTime.of(13, 30), 5, anomalyDay + 2, anomalyDay - 1);
        AuthenticationEventsGenerator eventGenerator_c =
                getScenarioGeneratorTemplate(isSrcMachine, TimeHostGenerator_c, eventIdGen, testUser + "_other", testUser + "_" + srcOrDstString + "_other_user_machine");
        AuthenticationOperationGenerator opGenerator_c = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator_c= new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_AUTHENTICATED_THROUGH_KERBEROS.value));
        opGenerator.setOperationTypeGenerator(opTypeGenerator_c);
        eventGenerator_c.setAuthenticationOperationGenerator(opGenerator_c);

        // GENERATOR4
        ITimeGenerator TimeHostGenerator_d =
                new MinutesIncrementTimeGenerator(LocalTime.of(12, 00), LocalTime.of(13, 30), 5, anomalyDay + 2, anomalyDay - 1);
        AuthenticationEventsGenerator eventGenerator_d =
                getScenarioGeneratorTemplate(isSrcMachine, TimeHostGenerator_d, eventIdGen, testUser + "_other", testUser + "_" + srcOrDstString + "_other_user_machine");
        AuthenticationOperationGenerator opGenerator_d = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator_d = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.REMOTE_INTERACTIVE.value));
        opGenerator.setOperationTypeGenerator(opTypeGenerator_d);
        eventGenerator_d.setAuthenticationOperationGenerator(opGenerator_d);

        // GENERATOR5
        ITimeGenerator TimeHostGenerator_e =
                new MinutesIncrementTimeGenerator(LocalTime.of(14, 00), LocalTime.of(15, 30), 5, anomalyDay + 2, anomalyDay - 1);
        AuthenticationEventsGenerator eventGenerator_e =
                getScenarioGeneratorTemplate(isSrcMachine, TimeHostGenerator_e, eventIdGen, testUser + "_other", testUser + "_" + srcOrDstString + "_other_user_machine");
        AuthenticationOperationGenerator opGenerator_e = new AuthenticationOperationGenerator();
        AuthenticationOperationTypeCyclicGenerator opTypeGenerator_e = new AuthenticationOperationTypeCyclicGenerator(new OperationType(AUTHENTICATION_OPERATION_TYPE.INTERACTIVE.value));
        opGenerator.setOperationTypeGenerator(opTypeGenerator_e);
        eventGenerator_e.setAuthenticationOperationGenerator(opGenerator_e);

        // Generate events
        List<AuthenticationEvent> events = eventGenerator_a.generate();
        events.addAll(eventGenerator_b.generate());
        events.addAll(eventGenerator_c.generate());
        events.addAll(eventGenerator_d.generate());
        events.addAll(eventGenerator_e.generate());

        return events;
    }


}
