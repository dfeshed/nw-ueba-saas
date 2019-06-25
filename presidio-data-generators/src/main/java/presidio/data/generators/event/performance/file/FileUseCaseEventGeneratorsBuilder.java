package presidio.data.generators.event.performance.file;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IOperationTypeGenerator;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.event.file.UserFileEventsGenerator;
import presidio.data.generators.event.performance.UserOrientedEventGeneratorsBuilder;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public abstract class FileUseCaseEventGeneratorsBuilder extends UserOrientedEventGeneratorsBuilder {

    protected static List<OperationType> ALL_OPERATION_TYPES;

    protected static List<OperationType> getAllOperationTypes(FILE_OPERATION_TYPE_CATEGORIES category){
        return getAllOperationTypes().stream()
                .filter(operationType ->
                        Collections.singletonList(category.value).equals(operationType.getCategories()))
                .collect(Collectors.toList());
    }

    private static List<OperationType> getAllOperationTypes(){
        if(ALL_OPERATION_TYPES == null) {
            ALL_OPERATION_TYPES = new ArrayList<>();
            addOperationTypes(ALL_OPERATION_TYPES,
                    FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION,
                    FILE_OPERATION_TYPE.FOLDER_RENAMED.value,
                    FILE_OPERATION_TYPE.FOLDER_OPENED.value,
                    FILE_OPERATION_TYPE.FOLDER_MOVED.value,
                    FILE_OPERATION_TYPE.FOLDER_DELETED.value,
                    FILE_OPERATION_TYPE.FOLDER_CREATED.value,
                    FILE_OPERATION_TYPE.FILE_RENAMED.value,
                    FILE_OPERATION_TYPE.FILE_OPENED.value,
                    FILE_OPERATION_TYPE.FILE_MOVED.value,
                    FILE_OPERATION_TYPE.FILE_DELETED.value,
                    FILE_OPERATION_TYPE.FILE_CREATED.value,
                    FILE_OPERATION_TYPE.FILE_COPIED.value,
                    FILE_OPERATION_TYPE.FILE_MODIFIED.value,
                    FILE_OPERATION_TYPE.FOLDER_MODIFIED.value,
                    FILE_OPERATION_TYPE.FILE_DOWNLOADED.value
            );
            addOperationTypes(ALL_OPERATION_TYPES,
                    FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE,
                    FILE_OPERATION_TYPE.LOCAL_SHARE_REMOVED.value,
                    FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value,
                    FILE_OPERATION_TYPE.FOLDER_OWNERSHIP_CHANGED.value,
                    FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED.value,
                    FILE_OPERATION_TYPE.FOLDER_CENTRAL_ACCESS_POLICY_CHANGED.value,
                    FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED.value,
                    FILE_OPERATION_TYPE.FILE_OWNERSHIP_CHANGED.value,
                    FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED.value,
                    FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED.value,
                    FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED.value,
                    FILE_OPERATION_TYPE.FOLDER_AUDITING_CHANGED.value,
                    FILE_OPERATION_TYPE.FILE_AUDITING_CHANGED.value,
                    FILE_OPERATION_TYPE.FILE_PERMISSION_CHANGED.value
            );
        }
        return ALL_OPERATION_TYPES;
    }

    private static void addOperationTypes(List<OperationType> operationTypes,
                                          FILE_OPERATION_TYPE_CATEGORIES category,
                                          String... operationTypeNames){
        for (String name: operationTypeNames){
            operationTypes.add(new OperationType(name, Collections.singletonList(category.value)));
        }
    }

    protected abstract String getUseCaseTestName();


    //machine generators
    private IMachineGenerator normalUserSrcMachinesGenerator;
    private IMachineGenerator normalUserAbnormalSrcMachinesGenerator;
    private IMachineGenerator adminUserSrcMachinesGenerator;
    private IMachineGenerator adminUserAbnormalSrcMachinesGenerator;
    private IMachineGenerator serviceAccountUserSrcMachinesGenerator;
    private IMachineGenerator serviceAccountUserAbnormalSrcMachinesGenerator;

    //file entity generators
    private IFileEntityGenerator normalUserSrcFileEntitiesGenerator;
    private IFileEntityGenerator normalUserAbnormalSrcFileEntitiesGenerator;
    private IFileEntityGenerator adminUserSrcFileEntitiesGenerator;
    private IFileEntityGenerator adminUserAbnormalSrcFileEntitiesGenerator;
    private IFileEntityGenerator serviceAccountUserSrcFileEntitiesGenerator;
    private IFileEntityGenerator serviceAccountUserAbnormalSrcFileEntitiesGenerator;
    private IFileEntityGenerator normalUserDstFileEntitiesGenerator;
    private IFileEntityGenerator normalUserAbnormalDstFileEntitiesGenerator;
    private IFileEntityGenerator adminUserDstFileEntitiesGenerator;
    private IFileEntityGenerator adminUserAbnormalDstFileEntitiesGenerator;
    private IFileEntityGenerator serviceAccountUserDstFileEntitiesGenerator;
    private IFileEntityGenerator serviceAccountUserAbnormalDstFileEntitiesGenerator;


    //Generator for normal users
    private FileEventsGenerator normalUsersEventGenerator;

    //Generator for admin users
    private FileEventsGenerator adminUsersEventGenerator;

    //Generator for service account users
    private FileEventsGenerator serviceAccountUsersEventGenerator;

    //Abnormal events Generator for normal users
    private FileEventsGenerator normalUsersAbnormalEventGenerator;

    //Abnormal events Generator for admin users
    private FileEventsGenerator adminUsersAbnormalEventGenerator;

    //Abnormal events Generator for service account users
    private FileEventsGenerator serviceAccountUsersAbnormalEventGenerator;




    public FileUseCaseEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                             List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                             List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                             IUserGenerator adminUserGenerator,
                                             List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                             List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                             IUserGenerator serviceAccountUserGenerator,
                                             List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                             IMachineGenerator normalUserSrcMachinesGenerator,
                                             IMachineGenerator normalUserAbnormalSrcMachinesGenerator,
                                             IMachineGenerator adminUserSrcMachinesGenerator,
                                             IMachineGenerator adminUserAbnormalSrcMachinesGenerator,
                                             IMachineGenerator serviceAccountUserSrcMachinesGenerator,
                                             IMachineGenerator serviceAccountUserAbnormalSrcMachinesGenerator,
                                             IFileEntityGenerator normalUserSrcFileEntitiesGenerator,
                                             IFileEntityGenerator normalUserAbnormalSrcFileEntitiesGenerator,
                                             IFileEntityGenerator adminUserSrcFileEntitiesGenerator,
                                             IFileEntityGenerator adminUserAbnormalSrcFileEntitiesGenerator,
                                             IFileEntityGenerator serviceAccountUserSrcFileEntitiesGenerator,
                                             IFileEntityGenerator serviceAccountUserAbnormalSrcFileEntitiesGenerator,
                                             IFileEntityGenerator normalUserDstFileEntitiesGenerator,
                                             IFileEntityGenerator normalUserAbnormalDstFileEntitiesGenerator,
                                             IFileEntityGenerator adminUserDstFileEntitiesGenerator,
                                             IFileEntityGenerator adminUserAbnormalDstFileEntitiesGenerator,
                                             IFileEntityGenerator serviceAccountUserDstFileEntitiesGenerator,
                                             IFileEntityGenerator serviceAccountUserAbnormalDstFileEntitiesGenerator) throws GeneratorException {

        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange,
                adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange,
                serviceAccountUserGenerator, serviceAcountUserActivityRange);
        this.normalUserSrcMachinesGenerator = normalUserSrcMachinesGenerator;
        this.normalUserAbnormalSrcMachinesGenerator = normalUserAbnormalSrcMachinesGenerator;
        this.adminUserSrcMachinesGenerator = adminUserSrcMachinesGenerator;
        this.adminUserAbnormalSrcMachinesGenerator = adminUserAbnormalSrcMachinesGenerator;
        this.serviceAccountUserSrcMachinesGenerator = serviceAccountUserSrcMachinesGenerator;
        this.serviceAccountUserAbnormalSrcMachinesGenerator = serviceAccountUserAbnormalSrcMachinesGenerator;
        this.normalUserSrcFileEntitiesGenerator = normalUserSrcFileEntitiesGenerator;
        this.normalUserAbnormalSrcFileEntitiesGenerator = normalUserAbnormalSrcFileEntitiesGenerator;
        this.adminUserSrcFileEntitiesGenerator = adminUserSrcFileEntitiesGenerator;
        this.adminUserAbnormalSrcFileEntitiesGenerator = adminUserAbnormalSrcFileEntitiesGenerator;
        this.serviceAccountUserSrcFileEntitiesGenerator = serviceAccountUserSrcFileEntitiesGenerator;
        this.serviceAccountUserAbnormalSrcFileEntitiesGenerator = serviceAccountUserAbnormalSrcFileEntitiesGenerator;
        this.normalUserDstFileEntitiesGenerator = normalUserDstFileEntitiesGenerator;
        this.normalUserAbnormalDstFileEntitiesGenerator = normalUserAbnormalDstFileEntitiesGenerator;
        this.adminUserDstFileEntitiesGenerator = adminUserDstFileEntitiesGenerator;
        this.adminUserAbnormalDstFileEntitiesGenerator = adminUserAbnormalDstFileEntitiesGenerator;
        this.serviceAccountUserDstFileEntitiesGenerator = serviceAccountUserDstFileEntitiesGenerator;
        this.serviceAccountUserAbnormalDstFileEntitiesGenerator = serviceAccountUserAbnormalDstFileEntitiesGenerator;

        createGenerators();
    }





    private void createGenerators() throws GeneratorException {





        /** GENERATORS: File **/


        //Event generator for normal users
        normalUsersEventGenerator =
                createEventGenerator(
                        normalUserSrcMachinesGenerator,
                        getFileOperationGenerator(normalUserSrcFileEntitiesGenerator,
                                normalUserDstFileEntitiesGenerator, getOperationTypeGeneratorForNormalbehavior()),
                        getUseCaseTestName() + "_normalUsersEventGenerator"
                );



        //Event Generator for admin users
        adminUsersEventGenerator =
                createEventGenerator(
                        adminUserSrcMachinesGenerator,
                        getFileOperationGenerator(adminUserSrcFileEntitiesGenerator,
                                adminUserDstFileEntitiesGenerator, getOperationTypeGeneratorForNormalbehavior()),
                        getUseCaseTestName() + "_adminUsersEventGenerator"
                );

        //Event generator for service account users
        serviceAccountUsersEventGenerator =
                createEventGenerator(
                        serviceAccountUserSrcMachinesGenerator,
                        getFileOperationGenerator(serviceAccountUserSrcFileEntitiesGenerator,
                                serviceAccountUserDstFileEntitiesGenerator, getOperationTypeGeneratorForNormalbehavior()),
                        getUseCaseTestName() + "_serviceAccountUsersEventGenerator"
                );



        //Abnormal events Generator for normal users
        normalUsersAbnormalEventGenerator =
                createEventGenerator(
                        normalUserAbnormalSrcMachinesGenerator,
                        getFileOperationGenerator(normalUserAbnormalSrcFileEntitiesGenerator,
                                normalUserAbnormalDstFileEntitiesGenerator,
                                getOperationTypeGeneratorForAbnormalBehavior()),
                        getUseCaseTestName() + "_normalUsersAbnormalEventGenerator"
                );




        //Abnormal events Generator for admin users
        adminUsersAbnormalEventGenerator =
                createEventGenerator(
                        adminUserAbnormalSrcMachinesGenerator,
                        getFileOperationGenerator(adminUserAbnormalSrcFileEntitiesGenerator,
                                adminUserAbnormalDstFileEntitiesGenerator,
                                getOperationTypeGeneratorForAbnormalBehavior()),
                        getUseCaseTestName() + "_adminUsersAbnormalEventGenerator"
                );


        //Abnormal events Generator for service account users
        serviceAccountUsersAbnormalEventGenerator =
                createEventGenerator(
                        serviceAccountUserAbnormalSrcMachinesGenerator,
                        getFileOperationGenerator(serviceAccountUserAbnormalSrcFileEntitiesGenerator,
                                serviceAccountUserAbnormalDstFileEntitiesGenerator,
                                getOperationTypeGeneratorForAbnormalBehavior()),
                        getUseCaseTestName() + "_serviceAccountUsersAbnormalEventGenerator"
                );

    }


    protected IFileOperationGenerator getFileOperationGenerator(IFileEntityGenerator srcFileEntitiesGenerator,
                                                                IFileEntityGenerator dstFileEntitiesGenerator,
                                                                IOperationTypeGenerator operationTypeGenerator) throws GeneratorException {
        FileOperationGenerator fileOperationGenerator = new FileOperationGenerator();
        fileOperationGenerator.setSourceFileEntityGenerator(srcFileEntitiesGenerator);
        fileOperationGenerator.setDestFileEntityGenerator(dstFileEntitiesGenerator);
        fileOperationGenerator.setOperationTypeGenerator(operationTypeGenerator);
        fileOperationGenerator.setOperationResultGenerator(getResultGenerator());
        return fileOperationGenerator;
    }






    protected abstract IOperationTypeGenerator getOperationTypeGeneratorForNormalbehavior();
    protected abstract IOperationTypeGenerator getOperationTypeGeneratorForAbnormalBehavior();
    protected abstract IStringGenerator getResultGenerator();

    //==================================================================================
    // Creating Event Generators for all events for all type of users (normal, admin, service account)
    // In the random generator which is built per day the time generator is added
    // In addition when building the random generator the set of users is randomly reduced.
    //==================================================================================

    private FileEventsGenerator createEventGenerator(
            IMachineGenerator machineGenerator,
            IFileOperationGenerator fileOperationGenerator,
            String generatorName) throws GeneratorException {
        UserFileEventsGenerator usrFileEventsGenerator = new UserFileEventsGenerator();
        usrFileEventsGenerator.setMachineEntityGenerator(machineGenerator);
        usrFileEventsGenerator.setFileOperationGenerator(fileOperationGenerator);
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(generatorName);
        usrFileEventsGenerator.setEventIdGenerator(eventIdGen);

        return usrFileEventsGenerator;
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
