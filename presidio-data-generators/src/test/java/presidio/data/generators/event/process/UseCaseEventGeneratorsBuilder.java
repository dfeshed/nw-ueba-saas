package presidio.data.generators.event.process;

import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.RandomMultiEventGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessEntityGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.LimitNumOfUsersGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class UseCaseEventGeneratorsBuilder extends ProcessEventGeneratorsBuilder{


    //Normal behavior + abnormal time.
    protected abstract int getNumOfNormalUsers();
    protected abstract int getNumOfNormalUsersDaily();
    protected abstract double getEventProbabilityForNormalUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForNormalUsers();
    protected abstract int getMinNumOfFilesPerNormalUserForSrcProcesses();
    protected abstract int getMaxNumOfFilesPerNormalUserForSrcProcesses();
    protected abstract int getMinNumOfFilesPerNormalUserForDestProcesses();
    protected abstract int getMaxNumOfFilesPerNormalUserForDestProcesses();

    protected abstract int getNumOfAdminUsers();
    protected abstract int getNumOfAdminUsersDaily();
    protected abstract double getEventProbabilityForAdminUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForAdminUsers();
    protected abstract int getMinNumOfFilesPerAdminUserForSrcProcesses();
    protected abstract int getMaxNumOfFilesPerAdminUserForSrcProcesses();
    protected abstract int getMinNumOfFilesPerAdminUserForDestProcesses();
    protected abstract int getMaxNumOfFilesPerAdminUserForDestProcesses();

    protected abstract int getNumOfServiceAccountUsers();
    protected abstract int getNumOfServiceAccountUsersDaily();
    protected abstract double getEventProbabilityForServiceAccountUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForServiceAccountUsers();
    protected abstract int getMinNumOfFilesPerServiceAccountUserForSrcProcesses();
    protected abstract int getMaxNumOfFilesPerServiceAccountUserForSrcProcesses();
    protected abstract int getMinNumOfFilesPerServiceAccountUserForDestProcesses();
    protected abstract int getMaxNumOfFilesPerServiceAccountUserForDestProcesses();

    protected abstract List<FileEntity> getSrcProcesses();
    protected abstract List<FileEntity> getDestProcesses();
    protected abstract String[] getOperationTypeNames();
    protected abstract String getUseCaseTestName();

    //abnormal behavior.
    protected abstract int getNumOfNormalUsersDailyForNonActiveWorkingHours();
    protected abstract double getEventProbabilityForNormalUsersForNonActiveWorkingHours();
    protected abstract int getMinNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents();
    protected abstract int getMinNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents();

    protected abstract int getNumOfAdminUsersDailyForNonActiveWorkingHours();
    protected abstract double getEventProbabilityForAdminUsersForNonActiveWorkingHours();
    protected abstract int getMinNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents();
    protected abstract int getMinNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents();

    protected abstract int getNumOfServiceAccountUsersDailyForAbnormalEvents();
    protected abstract double getEventProbabilityForServiceAccountUsersForAbnormalEvents();
    protected abstract int getMinNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents();
    protected abstract int getMinNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents();

    protected abstract IStringListGenerator getSrcProcessCategoriesGenerator(IFileEntityGenerator fileEntityGenerator);
    protected abstract IStringListGenerator getSrcProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator);
    protected abstract IStringListGenerator getDstProcessCategoriesGenerator(IFileEntityGenerator fileEntityGenerator);
    protected abstract IStringListGenerator getDstProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator);


    //users generators
    private IUserGenerator allNormalUsers;
    private IUserGenerator allAdminUsers;
    private IUserGenerator allServiceAccountUsers;


    //Generator for Processes and normal users
    private ProcessEventsGenerator normalUsersEventGenerator;

    //Generator for Processes and admin users
    private ProcessEventsGenerator adminUsersEventGenerator;

    //Generator for Processes and service account users
    private ProcessEventsGenerator serviceAccountUsersEventGenerator;

    //Abnormal events Generator for Processes and normal users
    ProcessEventsGenerator normalUsersAbnormalEventGenerator;

    //Abnormal events Generator for recon tool group A Processes and admin users
    ProcessEventsGenerator adminUsersAbnormalEventGenerator;

    //Abnormal events Generator for recon tool group A Processes and service account users
    ProcessEventsGenerator serviceAccountUsersAbnormalEventGenerator;


    public UseCaseEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                                 List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                                 List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                                 IUserGenerator adminUserGenerator,
                                                 List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                                 List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                                 IUserGenerator serviceAccountUserGenerator,
                                                 List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                                 IMachineGenerator machineGenerator,
                                                 List<FileEntity> nonImportantProcesses){

        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange,
                adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange,
                serviceAccountUserGenerator, serviceAcountUserActivityRange,
                machineGenerator, nonImportantProcesses);
        createGenerators();
    }


    private void createGenerators(){
        //users generators
        allNormalUsers = new LimitNumOfUsersGenerator((int)(getNumOfNormalUsers()*getUsersMultiplier()), normalUserGenerator);
        allAdminUsers = new LimitNumOfUsersGenerator((int)(getNumOfAdminUsers()*getUsersMultiplier()), adminUserGenerator);
        allServiceAccountUsers = new LimitNumOfUsersGenerator((int)(getNumOfServiceAccountUsers()*getUsersMultiplier()), serviceAccountUserGenerator);

        /** GENERATORS: PROCESS **/


        //Event generator for normal users
        normalUsersEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getSrcProcesses(),
                        getMinNumOfFilesPerNormalUserForSrcProcesses(),
                        getMaxNumOfFilesPerNormalUserForSrcProcesses(),
                        getDestProcesses(),
                        getMinNumOfFilesPerNormalUserForDestProcesses(),
                        getMaxNumOfFilesPerNormalUserForDestProcesses(),
                        getUseCaseTestName() + "_normalUsersEventGenerator"
                );



        //Event Generator for admin users
        adminUsersEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getSrcProcesses(),
                        getMinNumOfFilesPerAdminUserForSrcProcesses(),
                        getMaxNumOfFilesPerAdminUserForSrcProcesses(),
                        getDestProcesses(),
                        getMinNumOfFilesPerAdminUserForDestProcesses(),
                        getMaxNumOfFilesPerAdminUserForDestProcesses(),
                        getUseCaseTestName() + "_adminUsersEventGenerator"
                );

        //Event generator for service account users
        serviceAccountUsersEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getSrcProcesses(),
                        getMinNumOfFilesPerServiceAccountUserForSrcProcesses(),
                        getMaxNumOfFilesPerServiceAccountUserForSrcProcesses(),
                        getDestProcesses(),
                        getMinNumOfFilesPerServiceAccountUserForDestProcesses(),
                        getMaxNumOfFilesPerServiceAccountUserForDestProcesses(),
                        getUseCaseTestName() + "_serviceAccountUsersEventGenerator"
                );



        //Abnormal events Generator for recon tool group A Processes and normal users
        normalUsersAbnormalEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getSrcProcesses(),
                        getMinNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerNormalUserForSrcProcessesForAbnormalEvents(),
                        getDestProcesses(),
                        getMinNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerNormalUserForDestProcessesForAbnormalEvents(),
                        getUseCaseTestName() + "_normalUsersAbnormalEventGenerator"
                );




        //Abnormal events Generator for recon tool group A Processes and admin users
        adminUsersAbnormalEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getSrcProcesses(),
                        getMinNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerAdminUserForSrcProcessesForAbnormalEvents(),
                        getDestProcesses(),
                        getMinNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerAdminUserForDestProcessesForAbnormalEvents(),
                        getUseCaseTestName() + "_adminUsersAbnormalEventGenerator"
                );


        //Abnormal events Generator for recon tool group A Processes and service account users
        serviceAccountUsersAbnormalEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getSrcProcesses(),
                        getMinNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerServiceAccountUserForSrcProcessesForAbnormalEvents(),
                        getDestProcesses(),
                        getMinNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerServiceAccountUserForDestProcessesForAbnormalEvents(),
                        getUseCaseTestName() + "_serviceAccountUsersAbnormalEventGenerator"
                );

    }



    public List<AbstractEventGenerator<Event>> buildGenerators(Instant startInstant, Instant endInstant){
        List<AbstractEventGenerator<Event>> eventGenerators = new ArrayList<>();
        RandomMultiEventGenerator eventGenerator =
                createNormalUsersRandomEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createAdminUsersEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createServiceAccountUsersEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);

        //Abnormal events:
        eventGenerator =
                createNormalUsersRandomAbnormalEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createAdminUsersAbnormalEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createServiceAccountUsersAbnormalEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);

        return eventGenerators;
    }

    //==================================================================================
    // Creating Random Event Generators for all kind of users
    //==================================================================================



    private RandomMultiEventGenerator createNormalUsersRandomEventGenerator(Instant startInstant,
                                                                            Instant endInstant){
        IUserGenerator normalUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(getNumOfNormalUsersDaily()*getUsersMultiplier()), allNormalUsers);
        normalUsersEventGenerator.setUserGenerator(normalUsersDailyGenerator);
        return createRandomEventGenerator(normalUsersEventGenerator,
                normalUserActivityRange,
                getEventProbabilityForNormalUsers(),
                getTimeIntervalForNonActiveRangeForNormalUsers(),
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createNormalUsersRandomAbnormalEventGenerator(Instant startInstant,
                                                                                    Instant endInstant){
        IUserGenerator normalUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(getNumOfNormalUsersDailyForNonActiveWorkingHours()*getUsersMultiplier()), allNormalUsers);
        normalUsersAbnormalEventGenerator.setUserGenerator(normalUsersDailyGenerator);
        return createRandomEventGenerator(normalUsersAbnormalEventGenerator,
                normalUserAbnormalActivityRange,
                getEventProbabilityForNormalUsersForNonActiveWorkingHours(),
                getTimeIntervalForNonActiveRangeForNormalUsers(),
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createAdminUsersEventGenerator(Instant startInstant,
                                                                     Instant endInstant){
        IUserGenerator adminUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(getNumOfAdminUsersDaily()*getUsersMultiplier()), allAdminUsers);
        adminUsersEventGenerator.setUserGenerator(adminUsersDailyGenerator);
        return createRandomEventGenerator(adminUsersEventGenerator,
                adminUserActivityRange,
                getEventProbabilityForAdminUsers(),
                getTimeIntervalForNonActiveRangeForAdminUsers(),
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createAdminUsersAbnormalEventGenerator(Instant startInstant,
                                                                             Instant endInstant){
        IUserGenerator adminUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(getNumOfAdminUsersDailyForNonActiveWorkingHours()*getUsersMultiplier()), allAdminUsers);
        adminUsersAbnormalEventGenerator.setUserGenerator(adminUsersDailyGenerator);
        return createRandomEventGenerator(adminUsersAbnormalEventGenerator,
                adminUserAbnormalActivityRange,
                getEventProbabilityForAdminUsersForNonActiveWorkingHours(),
                getTimeIntervalForNonActiveRangeForAdminUsers(),
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createServiceAccountUsersEventGenerator(Instant startInstant,
                                                                              Instant endInstant){
        IUserGenerator serviceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(getNumOfServiceAccountUsersDaily()*getUsersMultiplier()), allServiceAccountUsers);
        serviceAccountUsersEventGenerator.setUserGenerator(serviceAccountUsersDailyGenerator);
        return createRandomEventGenerator(serviceAccountUsersEventGenerator,
                serviceAcountUserActivityRange,
                getEventProbabilityForServiceAccountUsers(),
                getTimeIntervalForNonActiveRangeForServiceAccountUsers(),
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createServiceAccountUsersAbnormalEventGenerator(Instant startInstant,
                                                                                      Instant endInstant){
        IUserGenerator serviceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(getNumOfServiceAccountUsersDailyForAbnormalEvents()*getUsersMultiplier()), allServiceAccountUsers);
        serviceAccountUsersAbnormalEventGenerator.setUserGenerator(serviceAccountUsersDailyGenerator);
        return createRandomEventGenerator(serviceAccountUsersAbnormalEventGenerator,
                serviceAcountUserActivityRange,
                getEventProbabilityForServiceAccountUsersForAbnormalEvents(),
                getTimeIntervalForNonActiveRangeForServiceAccountUsers(),
                startInstant,
                endInstant
        );
    }





    //==================================================================================
    // Creating Event Generators for all events for all type of users (normal, admin, service account)
    // In the random generator which is built per day the time generator is added
    // In addition when building the random generator the set of users is randomly reduced.
    //==================================================================================

    private ProcessEventsGenerator createEventGenerator(IMachineGenerator machineGenerator,
                                                        List<FileEntity> srcFileProcesses,
                                                        int minNumOfFilesPerUserForNonImportantProcesses,
                                                        int maxNumOfFilesPerUserForNonImportantProcesses,
                                                        List<FileEntity> dstFileProcesses,
                                                        int minNumOfFilesPerUserForReconTool,
                                                        int maxNumOfFilesPerUserForReconTool,
                                                        String generatorName) {
        UserProcessEventsGenerator processNormalUsrEventsGenerator = new UserProcessEventsGenerator();
        processNormalUsrEventsGenerator.setMachineEntityGenerator(machineGenerator);


        ProcessEntityGenerator srcProcessEntityGenerator = new ProcessEntityGenerator();
        IFileEntityGenerator srcFileEntityGenerator = new UserFileEntityGenerator(srcFileProcesses,
                minNumOfFilesPerUserForNonImportantProcesses, maxNumOfFilesPerUserForNonImportantProcesses);
        srcProcessEntityGenerator.setProcessFileGenerator(srcFileEntityGenerator);
        IStringListGenerator srcCategoriesGenerator = getSrcProcessCategoriesGenerator(srcFileEntityGenerator);
        srcProcessEntityGenerator.setProcessCategoriesGenerator(srcCategoriesGenerator);
        IStringListGenerator srcProcessDirectoryGroupsGenerator = getSrcProcessDirectoryGroupsGenerator(srcFileEntityGenerator);
        srcProcessEntityGenerator.setProcessDirectoryGroupsGenerator(srcProcessDirectoryGroupsGenerator);

        ProcessEntityGenerator dstProcessEntityGenerator = new ProcessEntityGenerator();
        IFileEntityGenerator dstFileEntityGenerator = new UserFileEntityGenerator(dstFileProcesses,
                minNumOfFilesPerUserForReconTool, maxNumOfFilesPerUserForReconTool);
        dstProcessEntityGenerator.setProcessFileGenerator(dstFileEntityGenerator);

        IStringListGenerator destCategoriesGenerator = getDstProcessCategoriesGenerator(dstFileEntityGenerator);
        dstProcessEntityGenerator.setProcessCategoriesGenerator(destCategoriesGenerator);
        IStringListGenerator dstProcessDirectoryGroupsGenerator = getDstProcessDirectoryGroupsGenerator(dstFileEntityGenerator);
        dstProcessEntityGenerator.setProcessDirectoryGroupsGenerator(dstProcessDirectoryGroupsGenerator);


        fillProcessEventsGeneratorWithDefaultGenerators(processNormalUsrEventsGenerator, srcProcessEntityGenerator,
                dstProcessEntityGenerator, getOperationTypeNames(), generatorName);

        return processNormalUsrEventsGenerator;
    }


}
