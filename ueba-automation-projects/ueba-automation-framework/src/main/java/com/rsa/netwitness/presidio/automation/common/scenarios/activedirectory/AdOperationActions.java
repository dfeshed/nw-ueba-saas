package com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.activedirectoryop.ActiveDirOperationTypeCyclicGenerator;
import presidio.data.generators.activedirectoryop.ActiveDirectoryOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.event.activedirectory.ActiveDirectoryEventsGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.SingleAdminUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdOperationActions {

    public static List<ActiveDirectoryEvent> getEventsByOperationName(String opName, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        switch (opName) {
            case "CustomOperations":
                return getAdCustomOperation(eventIdGen, timeGenerator, userGenerator);
            case "GroupMembershipOperations":
                return getAdGroupMembershipOperation(eventIdGen, timeGenerator, userGenerator);
            case "SensitiveGroupMembershipOperations":
                return getAdSensitiveGroupMembershipOperation(eventIdGen, timeGenerator, userGenerator);
            case "NotSensitiveGroupMembershipOperations":
                return getAdNotSensitiveGroupMembershipOperation(eventIdGen, timeGenerator, userGenerator);
            case "SuccessfulADOperations":
                return getAdSuccessfulOperation(eventIdGen, timeGenerator, userGenerator);
            case "SuccessfulSensitiveOperations":
                return getAdSuccessfulSensitiveOperation(eventIdGen, timeGenerator, userGenerator);
            case "FailedADOperations":
                return getAdFailedOperation(eventIdGen, timeGenerator, userGenerator);
            case "ProtectedADOperations":
                return getAdProtectedOperation(eventIdGen, timeGenerator, userGenerator);
            case "FailedADOperationsInitiatorUser":
                return getAdFailedOperationInitiatorUser(eventIdGen, timeGenerator, userGenerator);
            default:
                return new ArrayList<>();
        }
    }
    /*********************************    Active Directory operations:    *********************************/
    // should be moved to ActiveDirectoryOpGeneratorTemplateFactory in the repository presidio-test-utils
    public static List<ActiveDirectoryEvent> getAdGroupMembershipOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        String[] operationTypeNames = {AD_OPERATION_TYPE.MEMBER_ADDED_TO_GROUP.value, AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value};
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(getOperationTypes(operationTypeNames, Collections.singletonList("GROUP_MEMBERSHIP_OPERATION")));

        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.FAILURE.value}, new int[] {100});

        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        opGenerator.setResultGenerator(opResultGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getAdSensitiveGroupMembershipOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        String[] operationTypeNames = {AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP.value,
                AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_CREATED.value,
                AD_OPERATION_TYPE.SECURITY_ENABLED_GLOBAL_GROUP_DELETED.value};
        List<String> categories = Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value,
                ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_OPERATION.value);
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(getOperationTypes(operationTypeNames, categories));

        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }
    public static List<ActiveDirectoryEvent> getAdNotSensitiveGroupMembershipOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        String[] operationTypeNames = {AD_OPERATION_TYPE.USER_MEMBER_OF_ADDED.value,
                AD_OPERATION_TYPE.USER_MEMBER_OF_REMOVED.value };
        List<String> categories = Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_OPERATION.value);
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(getOperationTypes(operationTypeNames, categories));

        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    private static OperationType[] getOperationTypes(String[] operationTypeNames, List<String> categories){
        List<OperationType> operationTypes = Arrays.asList(operationTypeNames).stream().map(s -> new OperationType(s, categories)).collect(Collectors.toList());

        return operationTypes.toArray(new OperationType[operationTypes.size()]);
    }

    public static List<ActiveDirectoryEvent> getAdSuccessfulOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.SUCCESS.value}, new int[] {100});
        opGenerator.setResultGenerator(opResultGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getAdSuccessfulSensitiveOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.SUCCESS.value}, new int[] {100});
        opGenerator.setResultGenerator(opResultGenerator);

        String[] operationTypeNames = {AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP.value,
                AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP.value};
        List<String> categories = Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value, "some_additional_test_category");
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(getOperationTypes(operationTypeNames, categories));

        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getAdSuccessfulUserPasswordChangedOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, SingleAdminUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.SUCCESS.value}, new int[] {100});
        opGenerator.setResultGenerator(opResultGenerator);

        String[] operationTypeNames = {AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value};
        List<String> categories = Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value, "some_additional_test_category");
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(getOperationTypes(operationTypeNames, categories));

        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }
    public static List<ActiveDirectoryEvent> getAdFailedOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // TODO: Fix ActiveDirectoryEventsGenerator - remove event.result and event.resultCode fields, they are part of operation field
        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.FAILURE.value}, new int[] {100});
        opGenerator.setResultGenerator(opResultGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getAdProtectedOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {"Protected"}, new int[] {100});
        opGenerator.setResultGenerator(opResultGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getAdFailedOperationInitiatorUser(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setInitiatorUserGenerator(new SingleUserGenerator("initiator_ad_user", "initiator_ad_user_id", "initiator_ad_user@initiator.com"));

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.FAILURE.value}, new int[] {100});
        opGenerator.setResultGenerator(opResultGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getAdCustomOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        // TODO: need to support "some custom operation"
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(AD_OPERATION_TYPE.MEMBER_ADDED_TO_GROUP.value);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getAdCustomOperationsList(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator, OperationType[] operations) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(operations);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getOperationFailedScenario(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        String[] operationTypeNames = {AD_OPERATION_TYPE.MEMBER_ADDED_TO_GROUP.value};
        List<String> categories = Collections.singletonList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_OPERATION.value);
        ActiveDirOperationTypeCyclicGenerator operationTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(getOperationTypes(operationTypeNames, categories));
        OperationResultPercentageGenerator resultPercentageGenerator =
                new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.FAILURE.value, OPERATION_RESULT.SUCCESS.value},
                new int[] {3,1});

        opGenerator.setOperationTypeGenerator(operationTypeGenerator);
        opGenerator.setResultGenerator(resultPercentageGenerator);
        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }
    public static List<ActiveDirectoryEvent> getAccountUnlockScenario(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, SingleUserGenerator userGenerator) throws GeneratorException {
        /**
        * USER_ACCOUNT_LOCKED
        * USER_ACCOUNT_UNLOCKED
        * PASSWORD_CHANGED_BY_NON_OWNER
        */
        timeGenerator.reset();
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        String[] operationTypeNames = {AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value, AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value, AD_OPERATION_TYPE.USER_PASSWORD_CHANGED_BY_NON_OWNER.value};
        List<String> categories = Collections.singletonList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value);
        ActiveDirOperationTypeCyclicGenerator operationTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(getOperationTypes(operationTypeNames, categories));

        opGenerator.setOperationTypeGenerator(operationTypeGenerator);
        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> getAdminAccountManipulationScenario(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        /**
         * User - admin
         * PASSWORD_CHANGED
         * MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP
         * USER_ACCOUNT_DISABLED
         * */

        timeGenerator.reset();
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();

        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        String[] operationTypeNames = {AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value, AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value};
        List<String> categories = Collections.singletonList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value);
        ActiveDirOperationTypeCyclicGenerator operationTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(getOperationTypes(operationTypeNames, categories));

        opGenerator.setOperationTypeGenerator(operationTypeGenerator);
        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    public static List<ActiveDirectoryEvent> alertsSanityTestEvents(int historicalStartDay, int anomalyDay) throws GeneratorException {
        List<ActiveDirectoryEvent> events = new ArrayList<>();

        final String testUser1 = "e2e_ad_time_anomaly";
        final String testUser2 = "e2e_ad_locked";
        final String testUser3 = "e2e_ad_admin";
        final String testCase = "e2e_ad_alerts";

        /** 5 days of normal activity:
         * User 1, 2, 3:
         * successful not sensitive operation once per hour - MEMBER_ADDED_TO_GROUP
         *
         * 3 days of anomalies:
         * User 1:
         * + abnormal time
         *
         * User 2:
         * + failed authentication - 3 times in hour
         * + USER_ACCOUNT_LOCKED
         * + USER_ACCOUNT_UNLOCKED
         * + PASSWORD_CHANGED_BY_NON_OWNER
         *
         * User 3, admin:
         * + PASSWORD_CHANGED
         * + MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP
         * + USER_ACCOUNT_DISABLED
         *
         * */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testCase);

        ITimeGenerator normalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(0,0), LocalTime.of(23,59), 40, historicalStartDay, anomalyDay);
        ITimeGenerator normalTimeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(3,0), LocalTime.of(21,59), 30, historicalStartDay, anomalyDay);
        ITimeGenerator abnormalTimeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(0,0), LocalTime.of(1,0), 5, anomalyDay, anomalyDay - 1);
        ITimeGenerator abnormalTimeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(10,0), LocalTime.of(11,0), 19, anomalyDay, anomalyDay - 1);
        ITimeGenerator abnormalTimeGenerator2_cont = new MinutesIncrementTimeGenerator(LocalTime.of(11,0), LocalTime.of(12,0), 19, anomalyDay, anomalyDay - 1);
        ITimeGenerator abnormalTimeGenerator3 = new MinutesIncrementTimeGenerator(LocalTime.of(13,0), LocalTime.of(14,0), 10, anomalyDay, anomalyDay - 1);

        SingleUserGenerator userGenerator1 = new SingleUserGenerator(testUser1);
        SingleUserGenerator userGenerator2 = new SingleUserGenerator(testUser2);
        IUserGenerator userGenerator3 = new SingleAdminUserGenerator(testUser3);

        // Normal:
        events.addAll(AdOperationActions.getEventsByOperationName("GroupMembershipOperations", eventIdGen, normalTimeGenerator1, userGenerator1));
        events.addAll(AdOperationActions.getEventsByOperationName("GroupMembershipOperations", eventIdGen, normalTimeGenerator, userGenerator2));
        events.addAll(AdOperationActions.getEventsByOperationName("GroupMembershipOperations", eventIdGen, normalTimeGenerator, userGenerator3));

        // Anomalies:
        events.addAll(AdOperationActions.getEventsByOperationName("GroupMembershipOperations", eventIdGen, abnormalTimeGenerator1, userGenerator1));
        events.addAll(AdOperationActions.getOperationFailedScenario(eventIdGen, abnormalTimeGenerator2, userGenerator2));
        events.addAll(AdOperationActions.getAccountUnlockScenario(eventIdGen, abnormalTimeGenerator2_cont, userGenerator2));

        events.addAll(AdOperationActions.getAdminAccountManipulationScenario(eventIdGen, abnormalTimeGenerator3, userGenerator3));

        return events;
    }

}
