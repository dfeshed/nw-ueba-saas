package com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.activedirectoryop.ActiveDirOperationTypeCyclicGenerator;
import presidio.data.generators.activedirectoryop.ActiveDirectoryOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.activedirectory.ActiveDirectoryEventsGenerator;
import presidio.data.generators.user.SingleAdminUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by presidio on 8/15/17.
 */
public class AdOperationTypeAnomalies {

    /*********************************    Static score 100 on Operation Types:    *********************************/

    public static List<ActiveDirectoryEvent> getAllActiveDirOperations(String testUser, ITimeGenerator timeGenerator) throws GeneratorException {
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser); // Not admin

        timeGenerator.reset();

        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        List<ActiveDirectoryEvent> events = eventGenerator.generate();

        return events;
    }

    /*********************************    Admin changed his own password:    *********************************/
    public static List<ActiveDirectoryEvent> getAdminChangedHisPassword(String testUser, ITimeGenerator timeGenerator) throws GeneratorException {

        timeGenerator.reset();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleAdminUserGenerator userGenerator = new SingleAdminUserGenerator(testUser);

        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        List<ActiveDirectoryEvent> events = eventGenerator.generate();
        return events;
    }

    /*********************************    Static Ps for Smart:    *********************************/
    public static List<ActiveDirectoryEvent> getOneOperation4StaticPs(String testUser, ITimeGenerator timeGenerator, AD_OPERATION_TYPE operationType, String[] opTypeCategories) throws GeneratorException {

        timeGenerator.reset();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleAdminUserGenerator userGenerator = new SingleAdminUserGenerator(testUser);

        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(new OperationType(operationType.value, Arrays.asList(opTypeCategories)));
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        List<ActiveDirectoryEvent> events = eventGenerator.generate();
        return events;
    }

    public static List<ActiveDirectoryEvent> getAllOperation4StaticPs(String testUser, ITimeGenerator timeGenerator) throws GeneratorException {

        timeGenerator.reset();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleAdminUserGenerator userGenerator = new SingleAdminUserGenerator(testUser);

        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        List<ActiveDirectoryEvent> events = eventGenerator.generate();
        return events;
    }

    public static List<ActiveDirectoryEvent> getNormalOperation4StaticPs(String testUser, ITimeGenerator timeGenerator) throws GeneratorException {

        timeGenerator.reset();

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleAdminUserGenerator userGenerator = new SingleAdminUserGenerator(testUser);

        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(new OperationType(AD_OPERATION_TYPE.MEMBER_ADDED_TO_GROUP.value, Collections.singletonList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_OPERATION.value)));
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        eventGenerator.setActiveDirOperationGenerator(opGenerator);
        List<ActiveDirectoryEvent> events = eventGenerator.generate();
        return events;
    }

    /*********************************    All, including new Operation Types:    *********************************/

    public static List<ActiveDirectoryEvent> getCustomActiveDirOperations(String testUser) throws GeneratorException {

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser); // Not admin

        List<OperationType> operationTypes = buildOperationTypesList();
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(operationTypes.toArray(new OperationType[0]));
        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        ActiveDirectoryEventsGenerator eventGenerator = new ActiveDirectoryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setActiveDirOperationGenerator(opGenerator);

        List<ActiveDirectoryEvent> events = eventGenerator.generate();

        return events;
    }

    private static List<OperationType> buildOperationTypesList() {
        List<OperationType> operationTypes = new ArrayList();
        operationTypes.add(new OperationType("COMPUTER_ACCOUNT_CREATED"));
        operationTypes.add(new OperationType("COMPUTER_ACCOUNT_CHANGED"));
        operationTypes.add(new OperationType("MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_LOCAL_GROUP_DELETED"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_LOCAL_GROUP_CHANGED"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_UNIVERSAL_GROUP_CHANGED"));
        operationTypes.add(new OperationType("USER_ACCOUNT_LOCKED"));
        operationTypes.add(new OperationType("ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD"));
        operationTypes.add(new OperationType("CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP"));
        operationTypes.add(new OperationType("CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP"));
        operationTypes.add(new OperationType("DIRECTORY_SERVICE_OBJECT_MODIFIED"));
        operationTypes.add(new OperationType("GROUP_TYPE_CHANGED"));
        operationTypes.add(new OperationType("PERMISSIONS_ON_OBJECT_CHANGED"));
        operationTypes.add(new OperationType("COMPUTER_ACCOUNT_DELETED"));
        operationTypes.add(new OperationType("DOMAIN_POLICY_CHANGED"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_GLOBAL_GROUP_CHANGED"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_GLOBAL_GROUP_CREATED"));
        operationTypes.add(new OperationType("MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED"));
        operationTypes.add(new OperationType("MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED"));
        operationTypes.add(new OperationType("USER_ACCOUNT_CREATED"));
        operationTypes.add(new OperationType("USER_ACCOUNT_ENABLED"));
        operationTypes.add(new OperationType("USER_PASSWORD_CHANGED"));
        operationTypes.add(new OperationType("USER_PASSWORD_RESET"));
        operationTypes.add(new OperationType("USER_ACCOUNT_DISABLED"));
        operationTypes.add(new OperationType("USER_ACCOUNT_DELETED"));
        operationTypes.add(new OperationType("USER_ACCOUNT_CHANGED"));
        operationTypes.add(new OperationType("USER_ACCOUNT_UNLOCKED"));
        operationTypes.add(new OperationType("SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT"));
        operationTypes.add(new OperationType("MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_GLOBAL_GROUP_DELETED"));
        operationTypes.add(new OperationType("SECURITY_ENABLED_LOCAL_GROUP_CREATED"));
        operationTypes.add(new OperationType("MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP"));

        return operationTypes;
    }

}