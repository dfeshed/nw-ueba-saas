package presidio.data.generators.event.performance.active_directory;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.activedirectoryop.ActiveDirectoryOperationGenerator;
import presidio.data.generators.activedirectoryop.IActiveDirectoryOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IOperationTypeGenerator;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.activedirectory.ActiveDirectoryEventsGenerator;
import presidio.data.generators.event.activedirectory.UserActiveDirectoryEventsGenerator;
import presidio.data.generators.event.performance.UserOrientedEventGeneratorsBuilder;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.*;
import java.util.stream.Collectors;





public abstract class ActiveDirectoryUseCaseEventGeneratorsBuilder extends UserOrientedEventGeneratorsBuilder {

    private static List<OperationType> ALL_OPERATION_TYPES;

    protected static List<OperationType> getAllOperationTypes(FILE_OPERATION_TYPE_CATEGORIES category){
        return getAllOperationTypes().stream()
                .filter(operationType ->
                        operationType.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    protected static List<OperationType> getAllOperationTypes(){
        if(ALL_OPERATION_TYPES == null) {
            ALL_OPERATION_TYPES = getOperation2CategoryMap();
        }
        return ALL_OPERATION_TYPES;
    }

    private static List<OperationType> getOperation2CategoryMap()
    {
        List<OperationType> operationTypes = new ArrayList<>();

        addOperationTypes(operationTypes,AD_OPERATION_TYPE.ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.OBJECT_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.COMPUTER_ACCOUNT_CHANGED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.COMPUTER_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.COMPUTER_ACCOUNT_CREATED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.COMPUTER_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.COMPUTER_ACCOUNT_DELETED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.COMPUTER_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.OBJECT_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.OBJECT_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.DIRECTORY_SERVICE_OBJECT_MODIFIED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.OBJECT_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.DOMAIN_POLICY_CHANGED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.DOMAIN_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.GROUP_TYPE_CHANGED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_ADD_OPERATION.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_ADD_OPERATION.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_ADD_OPERATION.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_REMOVE_OPERATION.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_REMOVE_OPERATION.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_UNIVERSAL_GROUP.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_REMOVE_OPERATION.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.PERMISSIONS_ON_OBJECT_CHANGED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.OBJECT_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.SECURITY_ENABLED_GLOBAL_GROUP_CREATED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.SECURITY_ENABLED_GLOBAL_GROUP_DELETED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_CHANGED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_CREATED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_DELETED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.OBJECT_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_ACCOUNT_CHANGED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.USER_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_ACCOUNT_CREATED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.USER_MANAGEMENT.value));
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_ACCOUNT_DELETED.value, Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.USER_MANAGEMENT.value));
        /**
         * ==================================================================
         * The following are operation types without categories.
         * ==================================================================
         */
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value, Collections.emptyList());
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value, Collections.emptyList());
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value, Collections.emptyList());
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value, Collections.emptyList());
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value, Collections.emptyList());
        addOperationTypes(operationTypes,AD_OPERATION_TYPE.USER_PASSWORD_RESET.value, Collections.emptyList());

        return operationTypes;
    }


    private static void addOperationTypes(List<OperationType> operationTypes,
                                          String name,
                                          List<String> categories){
        operationTypes.add(new OperationType(name, categories));
    }

    protected abstract String getUseCaseTestName();





    //Generator for normal users
    private ActiveDirectoryEventsGenerator normalUsersEventGenerator;

    //Generator for admin users
    private ActiveDirectoryEventsGenerator adminUsersEventGenerator;

    //Generator for service account users
    private ActiveDirectoryEventsGenerator serviceAccountUsersEventGenerator;

    //Abnormal events Generator for normal users
    private ActiveDirectoryEventsGenerator normalUsersAbnormalEventGenerator;

    //Abnormal events Generator for admin users
    private ActiveDirectoryEventsGenerator adminUsersAbnormalEventGenerator;

    //Abnormal events Generator for service account users
    private ActiveDirectoryEventsGenerator serviceAccountUsersAbnormalEventGenerator;




    public ActiveDirectoryUseCaseEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                                        List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                                        List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                                        IUserGenerator adminUserGenerator,
                                                        List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                                        List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                                        IUserGenerator serviceAccountUserGenerator,
                                                        List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange) throws GeneratorException {

        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange,
                adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange,
                serviceAccountUserGenerator, serviceAcountUserActivityRange);

        createGenerators();
    }





    private void createGenerators() throws GeneratorException {





        /** GENERATORS: Active Directory **/


        //Event generator for normal users
        normalUsersEventGenerator =
                createEventGenerator(
                        getOperationGenerator(getOperationTypeGeneratorForNormalbehavior()),
                        getUseCaseTestName() + "_normalUsersEventGenerator"
                );



        //Event Generator for admin users
        adminUsersEventGenerator =
                createEventGenerator(
                        getOperationGenerator(getOperationTypeGeneratorForNormalbehavior()),
                        getUseCaseTestName() + "_adminUsersEventGenerator"
                );

        //Event generator for service account users
        serviceAccountUsersEventGenerator =
                createEventGenerator(
                        getOperationGenerator(getOperationTypeGeneratorForNormalbehavior()),
                        getUseCaseTestName() + "_serviceAccountUsersEventGenerator"
                );



        //Abnormal events Generator for normal users
        normalUsersAbnormalEventGenerator =
                createEventGenerator(
                        getOperationGenerator(getOperationTypeGeneratorForAbnormalBehavior()),
                        getUseCaseTestName() + "_normalUsersAbnormalEventGenerator"
                );




        //Abnormal events Generator for admin users
        adminUsersAbnormalEventGenerator =
                createEventGenerator(
                        getOperationGenerator(getOperationTypeGeneratorForAbnormalBehavior()),
                        getUseCaseTestName() + "_adminUsersAbnormalEventGenerator"
                );


        //Abnormal events Generator for service account users
        serviceAccountUsersAbnormalEventGenerator =
                createEventGenerator(
                        getOperationGenerator(getOperationTypeGeneratorForAbnormalBehavior()),
                        getUseCaseTestName() + "_serviceAccountUsersAbnormalEventGenerator"
                );

    }


    protected IActiveDirectoryOperationGenerator getOperationGenerator(IOperationTypeGenerator operationTypeGenerator) throws GeneratorException {
        ActiveDirectoryOperationGenerator operationGenerator = new ActiveDirectoryOperationGenerator();
        operationGenerator.setOperationTypeGenerator(operationTypeGenerator);
        operationGenerator.setResultGenerator(getResultGenerator());
        return operationGenerator;
    }






    protected abstract IOperationTypeGenerator getOperationTypeGeneratorForNormalbehavior();
    protected abstract IOperationTypeGenerator getOperationTypeGeneratorForAbnormalBehavior();
    protected abstract IStringGenerator getResultGenerator();

    //==================================================================================
    // Creating Event Generators for all events for all type of users (normal, admin, service account)
    // In the random generator which is built per day the time generator is added
    // In addition when building the random generator the set of users is randomly reduced.
    //==================================================================================

    private ActiveDirectoryEventsGenerator createEventGenerator(
            IActiveDirectoryOperationGenerator operationGenerator,
            String generatorName) throws GeneratorException {
        UserActiveDirectoryEventsGenerator usrActiveDirectoryEventsGenerator = new UserActiveDirectoryEventsGenerator();
        usrActiveDirectoryEventsGenerator.setActiveDirOperationGenerator(operationGenerator);
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(generatorName);
        usrActiveDirectoryEventsGenerator.setEventIdGenerator(eventIdGen);

        return usrActiveDirectoryEventsGenerator;
    }

    @Override
    protected AbstractEventGenerator<Event> getNormalUserEventGenerator(IUserGenerator normalUsersDailyGenerator){
        normalUsersEventGenerator.setUserGenerator(normalUsersDailyGenerator);
        return normalUsersEventGenerator;
    }

    @Override
    protected AbstractEventGenerator<Event> getNormalUsersAbnormalEventGenerator(IUserGenerator normalUsersDailyGenerator){
        normalUsersAbnormalEventGenerator.setUserGenerator(normalUsersDailyGenerator);
        return normalUsersAbnormalEventGenerator;
    }

    @Override
    protected AbstractEventGenerator<Event> getAdminUserEventGenerator(IUserGenerator adminUsersDailyGenerator){
        adminUsersEventGenerator.setUserGenerator(adminUsersDailyGenerator);
        return adminUsersEventGenerator;
    }

    @Override
    protected AbstractEventGenerator<Event> getAdminUsersAbnormalEventGenerator(IUserGenerator adminUsersDailyGenerator){
        adminUsersAbnormalEventGenerator.setUserGenerator(adminUsersDailyGenerator);
        return adminUsersAbnormalEventGenerator;
    }

    @Override
    protected AbstractEventGenerator<Event> getServiceAccountUserEventGenerator(IUserGenerator serviceAccountUsersDailyGenerator){
        serviceAccountUsersEventGenerator.setUserGenerator(serviceAccountUsersDailyGenerator);
        return serviceAccountUsersEventGenerator;
    }

    @Override
    protected AbstractEventGenerator<Event> getServiceAccountUsersAbnormalEventGenerator(IUserGenerator serviceAccountUsersDailyGenerator){
        serviceAccountUsersAbnormalEventGenerator.setUserGenerator(serviceAccountUsersDailyGenerator);
        return serviceAccountUsersAbnormalEventGenerator;
    }
}
