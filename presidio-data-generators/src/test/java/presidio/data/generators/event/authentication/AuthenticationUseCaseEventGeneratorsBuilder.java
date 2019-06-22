package presidio.data.generators.event.authentication;

import presidio.data.domain.event.Event;
import presidio.data.generators.authenticationop.IAuthenticationOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.UserOrientedEventGeneratorsBuilder;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;

public abstract class AuthenticationUseCaseEventGeneratorsBuilder extends UserOrientedEventGeneratorsBuilder {

    protected abstract String getUseCaseTestName();


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
