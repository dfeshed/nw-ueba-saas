package presidio.data.generators.event.authentication;

import presidio.data.generators.authenticationop.IAuthenticationOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.RandomMultiEventGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.LimitNumOfUsersGenerator;

import java.time.Instant;
import java.util.List;

public abstract class AuthenticationUseCaseEventGeneratorsBuilder extends AuthenticationEventGeneratorsBuilder{

    //Normal behavior + abnormal time.
    protected abstract int getNumOfNormalUsers();
    protected abstract int getNumOfNormalUsersDaily();
    protected abstract double getEventProbabilityForNormalUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForNormalUsers();


    protected abstract int getNumOfAdminUsers();
    protected abstract int getNumOfAdminUsersDaily();
    protected abstract double getEventProbabilityForAdminUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForAdminUsers();

    protected abstract int getNumOfServiceAccountUsers();
    protected abstract int getNumOfServiceAccountUsersDaily();
    protected abstract double getEventProbabilityForServiceAccountUsers();
    protected abstract int getTimeIntervalForNonActiveRangeForServiceAccountUsers();

    protected abstract String getUseCaseTestName();

    //abnormal behavior.
    protected abstract int getNumOfNormalUsersDailyForNonActiveWorkingHours();
    protected abstract double getEventProbabilityForNormalUsersForNonActiveWorkingHours();

    protected abstract int getNumOfAdminUsersDailyForNonActiveWorkingHours();
    protected abstract double getEventProbabilityForAdminUsersForNonActiveWorkingHours();

    protected abstract int getNumOfServiceAccountUsersDailyForNonActiveWorkingHours();
    protected abstract double getEventProbabilityForServiceAccountUsersForNonActiveWorkingHours();




    //users generators
    private IUserGenerator allNormalUsers;
    private IUserGenerator allAdminUsers;
    private IUserGenerator allServiceAccountUsers;

    //machine generators
    private IMachineGenerator normalUserSrcMachinesGenerator;
    private IMachineGenerator normalUserAbnormalSrcMachinesGenerator;
    private IMachineGenerator adminUserSrcMachinesGenerator;
    private IMachineGenerator adminUserAbnormalSrcMachinesGenerator;
    private IMachineGenerator serviceAccountUserSrcMachinesGenerator;
    private IMachineGenerator serviceAccountUserAbnormalSrcMachinesGenerator;

    private IMachineGenerator normalUserDstMachinesGenerator;
    private IMachineGenerator normalUserAbnormalDstMachinesGenerator;
    private IMachineGenerator adminUserDstMachinesGenerator;
    private IMachineGenerator adminUserAbnormalDstMachinesGenerator;
    private IMachineGenerator serviceAccountUserDstMachinesGenerator;
    private IMachineGenerator serviceAccountUserAbnormalDstMachinesGenerator;



    //Generator for normal users
    private AuthenticationEventsGenerator normalUsersEventGenerator;

    //Generator for admin users
    private AuthenticationEventsGenerator adminUsersEventGenerator;

    //Generator for service account users
    private AuthenticationEventsGenerator serviceAccountUsersEventGenerator;

    //Abnormal events Generator for normal users
    private AuthenticationEventsGenerator normalUsersAbnormalEventGenerator;

    //Abnormal events Generator for admin users
    private AuthenticationEventsGenerator adminUsersAbnormalEventGenerator;

    //Abnormal events Generator for service account users
    private AuthenticationEventsGenerator serviceAccountUsersAbnormalEventGenerator;


    public AuthenticationUseCaseEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
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
                                                       IMachineGenerator normalUserDstMachinesGenerator,
                                                       IMachineGenerator normalUserAbnormalDstMachinesGenerator,
                                                       IMachineGenerator adminUserDstMachinesGenerator,
                                                       IMachineGenerator adminUserAbnormalDstMachinesGenerator,
                                                       IMachineGenerator serviceAccountUserDstMachinesGenerator,
                                                       IMachineGenerator serviceAccountUserAbnormalDstMachinesGenerator) throws GeneratorException {

        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange,
                adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange,
                serviceAccountUserGenerator, serviceAcountUserActivityRange);
        this.normalUserSrcMachinesGenerator = normalUserSrcMachinesGenerator;
        this.normalUserAbnormalSrcMachinesGenerator = normalUserAbnormalSrcMachinesGenerator;
        this.adminUserSrcMachinesGenerator = adminUserSrcMachinesGenerator;
        this.adminUserAbnormalSrcMachinesGenerator = adminUserAbnormalSrcMachinesGenerator;
        this.serviceAccountUserSrcMachinesGenerator = serviceAccountUserSrcMachinesGenerator;
        this.serviceAccountUserAbnormalSrcMachinesGenerator = serviceAccountUserAbnormalSrcMachinesGenerator;
        this.normalUserDstMachinesGenerator = normalUserDstMachinesGenerator;
        this.normalUserAbnormalDstMachinesGenerator = normalUserAbnormalDstMachinesGenerator;
        this.adminUserDstMachinesGenerator = adminUserDstMachinesGenerator;
        this.adminUserAbnormalDstMachinesGenerator = adminUserAbnormalDstMachinesGenerator;
        this.serviceAccountUserDstMachinesGenerator = serviceAccountUserDstMachinesGenerator;
        this.serviceAccountUserAbnormalDstMachinesGenerator = serviceAccountUserAbnormalDstMachinesGenerator;
        createGenerators();
    }





    private void createGenerators() throws GeneratorException {
        //users generators
        allNormalUsers = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfNormalUsers()*getUsersMultiplier())), normalUserGenerator);
        allAdminUsers = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfAdminUsers()*getUsersMultiplier())), adminUserGenerator);
        allServiceAccountUsers = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfServiceAccountUsers()*getUsersMultiplier())), serviceAccountUserGenerator);





        /** GENERATORS: AUTHENTICATION **/


        //Event generator for normal users
        normalUsersEventGenerator =
                createEventGenerator(
                        normalUserSrcMachinesGenerator,
                        normalUserDstMachinesGenerator,
                        getUseCaseTestName() + "_normalUsersEventGenerator"
                );



        //Event Generator for admin users
        adminUsersEventGenerator =
                createEventGenerator(
                        adminUserSrcMachinesGenerator,
                        adminUserDstMachinesGenerator,
                        getUseCaseTestName() + "_adminUsersEventGenerator"
                );

        //Event generator for service account users
        serviceAccountUsersEventGenerator =
                createEventGenerator(
                        serviceAccountUserSrcMachinesGenerator,
                        serviceAccountUserDstMachinesGenerator,
                        getUseCaseTestName() + "_serviceAccountUsersEventGenerator"
                );



        //Abnormal events Generator for recon tool group A Processes and normal users
        normalUsersAbnormalEventGenerator =
                createEventGenerator(
                        normalUserAbnormalSrcMachinesGenerator,
                        normalUserAbnormalDstMachinesGenerator,
                        getUseCaseTestName() + "_normalUsersAbnormalEventGenerator"
                );




        //Abnormal events Generator for recon tool group A Processes and admin users
        adminUsersAbnormalEventGenerator =
                createEventGenerator(
                        adminUserAbnormalSrcMachinesGenerator,
                        adminUserAbnormalDstMachinesGenerator,
                        getUseCaseTestName() + "_adminUsersAbnormalEventGenerator"
                );


        //Abnormal events Generator for recon tool group A Processes and service account users
        serviceAccountUsersAbnormalEventGenerator =
                createEventGenerator(
                        serviceAccountUserAbnormalSrcMachinesGenerator,
                        serviceAccountUserAbnormalDstMachinesGenerator,
                        getUseCaseTestName() + "_serviceAccountUsersAbnormalEventGenerator"
                );

    }









    protected abstract IAuthenticationOperationGenerator getOperationGenerator();
    protected abstract IStringGenerator getResultGenerator();

    //==================================================================================
    // Creating Event Generators for all events for all type of users (normal, admin, service account)
    // In the random generator which is built per day the time generator is added
    // In addition when building the random generator the set of users is randomly reduced.
    //==================================================================================

    private AuthenticationEventsGenerator createEventGenerator(
            IMachineGenerator srcMachineGenerator,
            IMachineGenerator dstMachineGenerator,
            String generatorName) throws GeneratorException {
        UserAuthenticationEventsGenerator usrAuthenticationEventsGenerator = new UserAuthenticationEventsGenerator();
        usrAuthenticationEventsGenerator.setSrcMachineGenerator(srcMachineGenerator);
        usrAuthenticationEventsGenerator.setDstMachineGenerator(dstMachineGenerator);
        usrAuthenticationEventsGenerator.setAuthenticationOperationGenerator(getOperationGenerator());
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(generatorName);
        usrAuthenticationEventsGenerator.setEventIDGenerator(eventIdGen);
        usrAuthenticationEventsGenerator.setResultGenerator(getResultGenerator());

        return usrAuthenticationEventsGenerator;
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
        IUserGenerator serviceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfServiceAccountUsersDailyForNonActiveWorkingHours()*getUsersMultiplier())), allServiceAccountUsers);
        serviceAccountUsersAbnormalEventGenerator.setUserGenerator(serviceAccountUsersDailyGenerator);
        return createRandomEventGenerator(serviceAccountUsersAbnormalEventGenerator,
                serviceAcountUserActivityRange,
                getEventProbabilityForServiceAccountUsersForNonActiveWorkingHours(),
                getTimeIntervalForNonActiveRangeForServiceAccountUsers(),
                startInstant,
                endInstant
        );
    }

}
