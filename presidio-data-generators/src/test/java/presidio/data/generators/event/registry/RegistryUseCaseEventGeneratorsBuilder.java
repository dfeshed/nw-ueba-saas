package presidio.data.generators.event.registry;

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
import java.util.Map;


public abstract class RegistryUseCaseEventGeneratorsBuilder extends RegistryEventGeneratorsBuilder{


    //Normal behavior + abnormal time.
    protected abstract int getNumOfNormalUsers();
    protected abstract int getNumOfNormalUsersDaily();
    protected abstract double getEventProbabilityForNormalUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForNormalUsers();
    protected abstract int getMinNumOfFilesPerNormalUserForProcesses();
    protected abstract int getMaxNumOfFilesPerNormalUserForProcesses();

    protected abstract int getNumOfAdminUsers();
    protected abstract int getNumOfAdminUsersDaily();
    protected abstract double getEventProbabilityForAdminUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForAdminUsers();
    protected abstract int getMinNumOfFilesPerAdminUserForProcesses();
    protected abstract int getMaxNumOfFilesPerAdminUserForProcesses();

    protected abstract int getNumOfServiceAccountUsers();
    protected abstract int getNumOfServiceAccountUsersDaily();
    protected abstract double getEventProbabilityForServiceAccountUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForServiceAccountUsers();
    protected abstract int getMinNumOfFilesPerServiceAccountUserForProcesses();
    protected abstract int getMaxNumOfFilesPerServiceAccountUserForProcesses();

    protected abstract int getMinNumOfRegistryGroupsPerProcess();
    protected abstract int getMaxNumOfRegistryGroupsPerProcess();


    protected abstract List<FileEntity> getProcesses();
    protected abstract String[] getOperationTypeNames();
    protected abstract String getUseCaseTestName();

    //abnormal behavior.
    protected abstract int getNumOfNormalUsersDailyForNonActiveWorkingHours();
    protected abstract double getEventProbabilityForNormalUsersForNonActiveWorkingHours();
    protected abstract int getMinNumOfFilesPerNormalUserForProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerNormalUserForProcessesForAbnormalEvents();

    protected abstract int getNumOfAdminUsersDailyForNonActiveWorkingHours();
    protected abstract double getEventProbabilityForAdminUsersForNonActiveWorkingHours();
    protected abstract int getMinNumOfFilesPerAdminUserForProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerAdminUserForProcessesForAbnormalEvents();

    protected abstract int getNumOfServiceAccountUsersDailyForAbnormalEvents();
    protected abstract double getEventProbabilityForServiceAccountUsersForAbnormalEvents();
    protected abstract int getMinNumOfFilesPerServiceAccountUserForProcessesForAbnormalEvents();
    protected abstract int getMaxNumOfFilesPerServiceAccountUserForProcessesForAbnormalEvents();

    protected abstract IStringListGenerator getProcessCategoriesGenerator(IFileEntityGenerator fileEntityGenerator);
    protected abstract IStringListGenerator getProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator);



    //users generators
    private IUserGenerator allNormalUsers;
    private IUserGenerator allAdminUsers;
    private IUserGenerator allServiceAccountUsers;


    //Generator for Processes and normal users
    private RegistryEventsGenerator normalUsersEventGenerator;

    //Generator for Processes and admin users
    private RegistryEventsGenerator adminUsersEventGenerator;

    //Generator for Processes and service account users
    private RegistryEventsGenerator serviceAccountUsersEventGenerator;

    //Abnormal events Generator for Processes and normal users
    RegistryEventsGenerator normalUsersAbnormalEventGenerator;

    //Abnormal events Generator for recon tool group A Processes and admin users
    RegistryEventsGenerator adminUsersAbnormalEventGenerator;

    //Abnormal events Generator for recon tool group A Processes and service account users
    RegistryEventsGenerator serviceAccountUsersAbnormalEventGenerator;


    public RegistryUseCaseEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                         IUserGenerator adminUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                         IUserGenerator serviceAccountUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                         IMachineGenerator machineGenerator,
                                         List<FileEntity> nonImportantProcesses,
                                         Map<String, List<String>> registryKeyGroupToRegistryKey,
                                         Map<String, List<String>> registryKeyToValueNamesMap){

        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange,
                adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange,
                serviceAccountUserGenerator, serviceAcountUserActivityRange,
                machineGenerator, nonImportantProcesses,registryKeyGroupToRegistryKey,
                registryKeyToValueNamesMap);
        createGenerators();
    }


    private void createGenerators(){
        //users generators
        allNormalUsers = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfNormalUsers()*getUsersMultiplier())), normalUserGenerator);
        allAdminUsers = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfAdminUsers()*getUsersMultiplier())), adminUserGenerator);
        allServiceAccountUsers = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfServiceAccountUsers()*getUsersMultiplier())), serviceAccountUserGenerator);

        /** GENERATORS: PROCESS **/


        //Event generator for normal users
        normalUsersEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getProcesses(),
                        getMinNumOfFilesPerNormalUserForProcesses(),
                        getMaxNumOfFilesPerNormalUserForProcesses(),
                        getUseCaseTestName() + "_normalUsersEventGenerator"
                );



        //Event Generator for admin users
        adminUsersEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getProcesses(),
                        getMinNumOfFilesPerAdminUserForProcesses(),
                        getMaxNumOfFilesPerAdminUserForProcesses(),
                        getUseCaseTestName() + "_adminUsersEventGenerator"
                );

        //Event generator for service account users
        serviceAccountUsersEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getProcesses(),
                        getMinNumOfFilesPerServiceAccountUserForProcesses(),
                        getMaxNumOfFilesPerServiceAccountUserForProcesses(),
                        getUseCaseTestName() + "_serviceAccountUsersEventGenerator"
                );



        //Abnormal events Generator for recon tool group A Processes and normal users
        normalUsersAbnormalEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getProcesses(),
                        getMinNumOfFilesPerNormalUserForProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerNormalUserForProcessesForAbnormalEvents(),
                        getUseCaseTestName() + "_normalUsersAbnormalEventGenerator"
                );




        //Abnormal events Generator for recon tool group A Processes and admin users
        adminUsersAbnormalEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getProcesses(),
                        getMinNumOfFilesPerAdminUserForProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerAdminUserForProcessesForAbnormalEvents(),
                        getUseCaseTestName() + "_adminUsersAbnormalEventGenerator"
                );


        //Abnormal events Generator for recon tool group A Processes and service account users
        serviceAccountUsersAbnormalEventGenerator =
                createEventGenerator(
                        machineGenerator,
                        getProcesses(),
                        getMinNumOfFilesPerServiceAccountUserForProcessesForAbnormalEvents(),
                        getMaxNumOfFilesPerServiceAccountUserForProcessesForAbnormalEvents(),
                        getUseCaseTestName() + "_serviceAccountUsersAbnormalEventGenerator"
                );

    }

    //==================================================================================
    // Creating Random Event Generators for all kind of users
    //==================================================================================



    protected RandomMultiEventGenerator createNormalUsersRandomEventGenerator(Instant startInstant,
                                                                            Instant endInstant){
        IUserGenerator normalUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfNormalUsersDaily()*getUsersMultiplier())), allNormalUsers);
        normalUsersEventGenerator.setUserGenerator(normalUsersDailyGenerator);
        return createRandomEventGenerator(normalUsersEventGenerator,
                normalUserActivityRange,
                getEventProbabilityForNormalUsers(),
                getTimeIntervalForNonActiveRangeForNormalUsers(),
                startInstant,
                endInstant
        );
    }

    protected RandomMultiEventGenerator createNormalUsersRandomAbnormalEventGenerator(Instant startInstant,
                                                                                    Instant endInstant){
        IUserGenerator normalUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfNormalUsersDailyForNonActiveWorkingHours()*getUsersMultiplier())), allNormalUsers);
        normalUsersAbnormalEventGenerator.setUserGenerator(normalUsersDailyGenerator);
        return createRandomEventGenerator(normalUsersAbnormalEventGenerator,
                normalUserAbnormalActivityRange,
                getEventProbabilityForNormalUsersForNonActiveWorkingHours(),
                getTimeIntervalForNonActiveRangeForNormalUsers(),
                startInstant,
                endInstant
        );
    }

    protected RandomMultiEventGenerator createAdminUsersEventGenerator(Instant startInstant,
                                                                     Instant endInstant){
        IUserGenerator adminUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfAdminUsersDaily()*getUsersMultiplier())), allAdminUsers);
        adminUsersEventGenerator.setUserGenerator(adminUsersDailyGenerator);
        return createRandomEventGenerator(adminUsersEventGenerator,
                adminUserActivityRange,
                getEventProbabilityForAdminUsers(),
                getTimeIntervalForNonActiveRangeForAdminUsers(),
                startInstant,
                endInstant
        );
    }

    protected RandomMultiEventGenerator createAdminUsersAbnormalEventGenerator(Instant startInstant,
                                                                             Instant endInstant){
        IUserGenerator adminUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfAdminUsersDailyForNonActiveWorkingHours()*getUsersMultiplier())), allAdminUsers);
        adminUsersAbnormalEventGenerator.setUserGenerator(adminUsersDailyGenerator);
        return createRandomEventGenerator(adminUsersAbnormalEventGenerator,
                adminUserAbnormalActivityRange,
                getEventProbabilityForAdminUsersForNonActiveWorkingHours(),
                getTimeIntervalForNonActiveRangeForAdminUsers(),
                startInstant,
                endInstant
        );
    }

    protected RandomMultiEventGenerator createServiceAccountUsersEventGenerator(Instant startInstant,
                                                                              Instant endInstant){
        IUserGenerator serviceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfServiceAccountUsersDaily()*getUsersMultiplier())), allServiceAccountUsers);
        serviceAccountUsersEventGenerator.setUserGenerator(serviceAccountUsersDailyGenerator);
        return createRandomEventGenerator(serviceAccountUsersEventGenerator,
                serviceAcountUserActivityRange,
                getEventProbabilityForServiceAccountUsers(),
                getTimeIntervalForNonActiveRangeForServiceAccountUsers(),
                startInstant,
                endInstant
        );
    }

    protected RandomMultiEventGenerator createServiceAccountUsersAbnormalEventGenerator(Instant startInstant,
                                                                                      Instant endInstant){
        IUserGenerator serviceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfServiceAccountUsersDailyForAbnormalEvents()*getUsersMultiplier())), allServiceAccountUsers);
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

    private RegistryEventsGenerator createEventGenerator(IMachineGenerator machineGenerator,
                                                        List<FileEntity> fileProcesses,
                                                        int minNumOfFilesPerUser,
                                                        int maxNumOfFilesPerUser,
                                                        String generatorName) {
        UserRegistryEventsGenerator usrEventsGenerator = new UserRegistryEventsGenerator();
        usrEventsGenerator.setMachineEntityGenerator(machineGenerator);


        ProcessEntityGenerator processEntityGenerator = new ProcessEntityGenerator();
        IFileEntityGenerator fileEntityGenerator =
                new UserFileEntityGenerator(fileProcesses, minNumOfFilesPerUser, maxNumOfFilesPerUser);
        processEntityGenerator.setProcessFileGenerator(fileEntityGenerator);
        IStringListGenerator srcCategoriesGenerator = getProcessCategoriesGenerator(fileEntityGenerator);
        processEntityGenerator.setProcessCategoriesGenerator(srcCategoriesGenerator);
        IStringListGenerator srcProcessDirectoryGroupsGenerator = getProcessDirectoryGroupsGenerator(fileEntityGenerator);
        processEntityGenerator.setProcessDirectoryGroupsGenerator(srcProcessDirectoryGroupsGenerator);

        ProcessRegistryEntryGenerator registryEntryGenerator =
                new ProcessRegistryEntryGenerator(
                        registryKeyGroupToRegistryKey,
                        registryKeyToValueNamesMap,
                        getMinNumOfRegistryGroupsPerProcess(),
                        getMaxNumOfRegistryGroupsPerProcess()
                );


        fillRegistryEventsGeneratorWithDefaultGenerators(
                usrEventsGenerator, processEntityGenerator,
                registryEntryGenerator, getOperationTypeNames(), generatorName);

        return usrEventsGenerator;
    }


}
