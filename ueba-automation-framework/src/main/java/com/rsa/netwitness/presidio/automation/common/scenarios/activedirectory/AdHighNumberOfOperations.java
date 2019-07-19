package com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory;

import presidio.data.domain.User;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.activedirectoryop.ActiveDirOperationTypeCyclicGenerator;
import presidio.data.generators.activedirectoryop.ActiveDirectoryOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IOperationTypeGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.activedirectory.ActiveDirectoryEventsGenerator;
import presidio.data.generators.user.SingleAdminUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdHighNumberOfOperations {
    public static List<ActiveDirectoryEvent> getHighNumSensitiveGroupMembershipEvents(String testUser, int anomalyDay) throws GeneratorException {
         return getHighNumSensitiveGroupMembershipEvents(testUser, anomalyDay, LocalTime.of(10, 30), LocalTime.of(14, 30), 1);
    }

    public static List<ActiveDirectoryEvent> getHighNumSensitiveGroupMembershipEvents(String testUser, int anomalyDay, LocalTime anomalyStartTime, LocalTime anomalyEndTime, int intervalMin) throws GeneratorException {
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getEventsByOperationName("SensitiveGroupMembershipOperations", eventIdGen, timeGenerator1, userGenerator));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(anomalyStartTime, anomalyEndTime, intervalMin, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("SensitiveGroupMembershipOperations", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<ActiveDirectoryEvent> getHighNumGroupMembershipEvents(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * context: <User, operationTypeCategories contains GROUP_MEMBERSHIP_OPERATION>
         * Aggregation Function: Number of Events
         *
         * Normal behavior: user performs normal (low) number of group membership operations
         * Anomaly: user performs high number of group membership operations
         */
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getEventsByOperationName("GroupMembershipOperations", eventIdGen, timeGenerator1, userGenerator));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(14, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("GroupMembershipOperations", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<ActiveDirectoryEvent> getAbnormalGroupChangesEvents(String testUser, int anomalyDay) throws GeneratorException {
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getAdCustomOperationsList(eventIdGen, timeGenerator1, userGenerator,
                new OperationType[] {
                    new OperationType(AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION", "SECURITY_SENSITIVE_OPERATION"}))}));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getAdCustomOperationsList(eventIdGen, timeGenerator2, userGenerator,
                new OperationType[] {
                        new OperationType(AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION", "SECURITY_SENSITIVE_OPERATION"}))}));

        ITimeGenerator timeGenerator3 = new MinutesIncrementTimeGenerator(LocalTime.of(20, 30), LocalTime.of(23, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getAdCustomOperationsList(eventIdGen, timeGenerator3, userGenerator,
                new OperationType[] {
                        new OperationType(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION", "SECURITY_SENSITIVE_OPERATION","GROUP_MEMBERSHIP_REMOVE_OPERATION"}))}));

        return events;

    }

    public static List<ActiveDirectoryEvent> getHighNumSuccessfulActiveDirectoryOperations(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * context: <User>
         * Aggregation Function: Number of result=success
         *
         * Normal behavior: user performs normal (low) number of successful active directory operations
         * Anomaly: user performs high number of successful active directory operations
         */
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getEventsByOperationName("SuccessfulADOperations", eventIdGen, timeGenerator1, userGenerator));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(10, 30), LocalTime.of(14, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("SuccessfulADOperations", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<ActiveDirectoryEvent> getHighNumSuccessfulSecuritySensitiveOperations(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * context: <User, operationTypeCategories contains SECURITY_SENSITIVE_OPERATION>
         * Aggregation Function: Number of result=success
         *
         * Normal behavior: user performs normal (low) number of successful security sensitive operations
         * Anomaly: user performs high number of successful security sensitive operations
         */
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getEventsByOperationName("SuccessfulSensitiveOperations", eventIdGen, timeGenerator1, userGenerator));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("SuccessfulSensitiveOperations", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<ActiveDirectoryEvent> getAdminChangedHisOwnPassword(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * context: <User, operationTypeCategories contains SECURITY_SENSITIVE_OPERATION>
         * Aggregation Function: Number of result=success
         *
         * Normal behavior: user performs normal (low) number of successful security sensitive operations
         * Anomaly: user performs high number of successful security sensitive operations
         */
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleAdminUserGenerator userGenerator = new SingleAdminUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getEventsByOperationName("SuccessfulSensitiveOperations", eventIdGen, timeGenerator1, userGenerator));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("SuccessfulSensitiveOperations", eventIdGen, timeGenerator2, userGenerator));

        ITimeGenerator timeGenerator3 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(13, 00), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getAdSuccessfulUserPasswordChangedOperation(eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<ActiveDirectoryEvent> getHighNumFailedActiveDirectoryEvents(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * context: <User>
         * Aggregation Function: Number of result=failure
         *
         * Normal behavior: user performs normal (low) number of failed active directory operations
         * Anomaly: user performs high number of failed active directory operations
         */
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getEventsByOperationName("FailedADOperations", eventIdGen, timeGenerator1, userGenerator));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(13, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("FailedADOperations", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<ActiveDirectoryEvent> getHighNumProtectedActiveDirectoryEvents(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * context: <User>
         * Aggregation Function: Number of result=failure
         *
         * Normal behavior: user performs normal (low) number of failed active directory operations
         * Anomaly: user performs high number of failed active directory operations
         */
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getEventsByOperationName("ProtectedADOperations", eventIdGen, timeGenerator1, userGenerator));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("ProtectedADOperations", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<ActiveDirectoryEvent> getHighNumFailedActiveDirectoryEvents(String testUser, ITimeGenerator normalTimeGenerator, ITimeGenerator abnormalTimeGenerator) throws GeneratorException {
        /**
         * context: <User>
         * Aggregation Function: Number of result=failure
         *
         * Normal behavior: user performs normal (low) number of failed active directory operations
         * Anomaly: user performs high number of failed active directory operations
         */
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        normalTimeGenerator.reset();
        abnormalTimeGenerator.reset();

        // Normal:
        events.addAll(AdOperationActions.getEventsByOperationName("FailedADOperations", eventIdGen, normalTimeGenerator, userGenerator));

        // Anomaly:
        events.addAll(AdOperationActions.getEventsByOperationName("FailedADOperations", eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<ActiveDirectoryEvent> getHighNumFailedActiveDirectoryInitiatorUserEvents(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * context: <User>
         * Aggregation Function: Number of result=failure
         *
         * Normal behavior: user performs normal (low) number of failed active directory operations
         * Anomaly: user performs high number of failed active directory operations
         */
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        events.addAll(AdOperationActions.getEventsByOperationName("FailedADOperationsInitiatorUser", eventIdGen, timeGenerator1, userGenerator));

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(13, 00), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("FailedADOperationsInitiatorUser", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }
    /*********************************    Abnormal Object Changed Operations:    *********************************/
    public static List<ActiveDirectoryEvent> getAbnormalObjectManagementOperations(String testUser, int anomalyDay) throws GeneratorException {
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleAdminUserGenerator userGenerator = new SingleAdminUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        ActiveDirectoryEventsGenerator eventGenerator1 = getSingleOperationActiveDirectoryEventsGeneratorTemplate(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_CHANGED);
        eventGenerator1.setUserGenerator(userGenerator);
        eventGenerator1.setEventIdGenerator(eventIdGen);
        eventGenerator1.setTimeGenerator(timeGenerator1);
        events.addAll(eventGenerator1.generate());

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        ActiveDirectoryEventsGenerator eventGenerator2 = getSingleOperationActiveDirectoryEventsGeneratorTemplate(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_CREATED);
        eventGenerator2.setUserGenerator(userGenerator);
        eventGenerator2.setEventIdGenerator(eventIdGen);
        eventGenerator2.setTimeGenerator(timeGenerator2);
        events.addAll(eventGenerator2.generate());

        return events;
    }

    private static ActiveDirectoryEventsGenerator getSingleOperationActiveDirectoryEventsGeneratorTemplate(AD_OPERATION_TYPE operationType) throws GeneratorException {
        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        IOperationTypeGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(operationType.value);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        eventGenerator.setActiveDirOperationGenerator(opGenerator);
        return eventGenerator;
    }

    /*******************************************     Multiple User Account Changes:    *******************************************/
    public static List<ActiveDirectoryEvent> getMultipleUserAccountChangesEvents(String testUser, int anomalyDay) throws GeneratorException {
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleAdminUserGenerator userGenerator = new SingleAdminUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 32, anomalyDay);
        ActiveDirectoryEventsGenerator eventGenerator1 = getSingleOperationActiveDirectoryEventsGeneratorTemplate(AD_OPERATION_TYPE.USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED);
        eventGenerator1.setUserGenerator(userGenerator);
        eventGenerator1.setEventIdGenerator(eventIdGen);
        eventGenerator1.setTimeGenerator(timeGenerator1);
        events.addAll(eventGenerator1.generate());

        // Anomaly:
        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(14, 30), 1, anomalyDay, anomalyDay - 1);
        ActiveDirectoryEventsGenerator eventGenerator2 = getSingleOperationActiveDirectoryEventsGeneratorTemplate(AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED);
        eventGenerator2.setUserGenerator(userGenerator);
        eventGenerator2.setEventIdGenerator(eventIdGen);
        eventGenerator2.setTimeGenerator(timeGenerator2);
        events.addAll(eventGenerator2.generate());

        return events;
    }

    public static List<ActiveDirectoryEvent> getActiveDirectoryUserAdmin(String testUser, int anomalyDay) throws GeneratorException {
        List<ActiveDirectoryEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        SingleAdminUserGenerator adminUserGenerator = new SingleAdminUserGenerator(testUser);

        User user = new User(testUser);
        user.setUserId(testUser);
        user.setFirstName("active directory");
        user.setLastName("admin");
        user.setAdministrator(true);

        ITimeGenerator timeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, 6, anomalyDay + 1);
        events.addAll(AdOperationActions.getEventsByOperationName("SuccessfulSensitiveOperations", eventIdGen, timeGenerator1, adminUserGenerator));

        events.get(events.size() - 1).setUser(user);

        ITimeGenerator timeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(20, 0), LocalTime.of(22, 0), 60, anomalyDay, anomalyDay - 1);
        events.addAll(AdOperationActions.getEventsByOperationName("FailedADOperations", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

}
