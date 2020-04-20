package presidio.data.generators.event.performance;

import presidio.data.domain.event.Event;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.RandomMultiEventGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.LimitNumOfUsersGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public abstract class UserOrientedEventGeneratorsBuilder extends EventGeneratorsBuilder {

    private static final long NANOS_PER_SECOND = 1000_000_000L;

    /** USERS **/
    private IUserGenerator allNormalUsers;
    private IUserGenerator builderAllNormalUsers;
    protected List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange;
    protected List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange;
    private IUserGenerator allAdminUsers;
    private IUserGenerator builderAllAdminUsers;
    protected List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange;
    protected List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange;
    private IUserGenerator allServiceAccountUsers;
    private IUserGenerator builderAllServiceAccountUsers;
    protected List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange;

    //users generators




    public UserOrientedEventGeneratorsBuilder(IUserGenerator allNormalUsers,
                                          List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                          List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                          IUserGenerator allAdminUsers,
                                          List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                          List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                          IUserGenerator allServiceAccountUsers,
                                          List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange){
        this.allNormalUsers = allNormalUsers;
        this.normalUserActivityRange = normalUserActivityRange;
        this.normalUserAbnormalActivityRange = normalUserAbnormalActivityRange;
        this.allAdminUsers = allAdminUsers;
        this.adminUserActivityRange = adminUserActivityRange;
        this.adminUserAbnormalActivityRange = adminUserAbnormalActivityRange;
        this.allServiceAccountUsers = allServiceAccountUsers;
        this.serviceAcountUserActivityRange = serviceAcountUserActivityRange;

        //users generators
        int numOfNormalUsers = (int)(Math.max(1,allNormalUsers.getMaxNumOfDistinctUsers()*getBuilderAllNormalUsersMultiplier()));
        builderAllNormalUsers = new LimitNumOfUsersGenerator(numOfNormalUsers, allNormalUsers);
        int numOfAdminUsers = (int)(Math.max(1,allAdminUsers.getMaxNumOfDistinctUsers()*getBuilderAllAdminUsersMultiplier()));
        builderAllAdminUsers = new LimitNumOfUsersGenerator(numOfAdminUsers, allAdminUsers);
        int numOfServiceAccountUsers = (int)(Math.max(1,allServiceAccountUsers.getMaxNumOfDistinctUsers()*getBuilderAllServiceAccountUsersMultiplier()));
        builderAllServiceAccountUsers = new LimitNumOfUsersGenerator(numOfServiceAccountUsers, allServiceAccountUsers);
    }

    //might be overiden by specific use case builder
    public double getBuilderAllNormalUsersMultiplier(){
        return 1;
    }

    //might be overiden by specific use case builder
    public double getBuilderAllAdminUsersMultiplier(){
        return 1;
    }

    //might be overiden by specific use case builder
    public double getBuilderAllServiceAccountUsersMultiplier(){
        return 1;
    }


    private long getNumOfNormalUsers() {
        return builderAllNormalUsers.getMaxNumOfDistinctUsers();
    }

    private int getNumOfAdminUsers() {
        return Math.toIntExact(builderAllAdminUsers.getMaxNumOfDistinctUsers());
    }

    private int getNumOfServiceAccountUsers() {
        return Math.toIntExact(builderAllServiceAccountUsers.getMaxNumOfDistinctUsers());
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

    //Normal behavior + abnormal time.


    protected abstract double getPercentOfNormalUserPerDayOutOfTotalAmountOfUsers();
    protected abstract double getNumOfEventsPerNormalUserPerHourOnAvg();
    protected abstract double getPercentOfAdminUserPerDayOutOfTotalAmountOfUsers();
    protected abstract double getNumOfEventsPerAdminUserPerHourOnAvg();
    protected abstract double getPercentOfServiceAccountUserPerDayOutOfTotalAmountOfUsers();
    protected abstract double getNumOfEventsPerServiceAccountUserPerHourOnAvg();
    protected abstract double getPercentOfNormalUserWithAnomaliesPerDayOutOfTotalAmountOfUsers();
    protected abstract double getNumOfEventsPerNormalUserWithAnomaliesPerHourOnAvg();
    protected abstract double getPercentOfAdminUserWithAnomaliesPerDayOutOfTotalAmountOfUsers();
    protected abstract double getNumOfEventsPerAdminUserWithAnomaliesPerHourOnAvg();
    protected abstract double getPercentOfServiceAccountUserWithAnomaliesPerDayOutOfTotalAmountOfUsers();
    protected abstract double getNumOfEventsPerServiceAccountUserWithAnomaliesPerHourOnAvg();

    private long getNumOfIterationsInAnHour(List<MultiRangeTimeGenerator.ActivityRange> activityRanges){
        return (3600*NANOS_PER_SECOND)/activityRanges.get(0).getDuration().toNanos();
    }

    private double getEventProbability(long numOfDailyUsers,
                                       double numOfEventsPerUserPerHourOnAvg,
                                       List<MultiRangeTimeGenerator.ActivityRange> activityRanges){
        double numOfHourlyEvents = numOfDailyUsers* numOfEventsPerUserPerHourOnAvg;
        return Math.min(1.0, numOfHourlyEvents/ getNumOfIterationsInAnHour(activityRanges));
    }

    private long getNumOfUsersDaily(long numOfUsers, double percentOfUserPerDayOutOfTotalAmountOfUsers) {
        return (long) Math.max(1, numOfUsers*percentOfUserPerDayOutOfTotalAmountOfUsers);
    }


    private long getNumOfNormalUsersDaily() {
        return getNumOfUsersDaily(getNumOfNormalUsers(), getPercentOfNormalUserPerDayOutOfTotalAmountOfUsers());
    }


    private double getEventProbabilityForNormalUsers() {
        return getEventProbability(getNumOfNormalUsersDaily(),
                getNumOfEventsPerNormalUserPerHourOnAvg(),
                normalUserActivityRange);
    }


    private int getTimeIntervalForNonActiveRangeForNormalUsers() {
        return 120000;
    }


    private int getNumOfAdminUsersDaily() {
        return (int) getNumOfUsersDaily(getNumOfAdminUsers(), getPercentOfAdminUserPerDayOutOfTotalAmountOfUsers());
    }


    private double getEventProbabilityForAdminUsers() {
        return getEventProbability(getNumOfAdminUsersDaily(),
                getNumOfEventsPerAdminUserPerHourOnAvg(),
                adminUserActivityRange);
    }


    private int getTimeIntervalForNonActiveRangeForAdminUsers() {
        return 120000;
    }







    private int getNumOfServiceAccountUsersDaily() {
        return (int) getNumOfUsersDaily(getNumOfServiceAccountUsers(), getPercentOfServiceAccountUserPerDayOutOfTotalAmountOfUsers());
    }


    private double getEventProbabilityForServiceAccountUsers() {
        return getEventProbability(getNumOfServiceAccountUsers(),
                getNumOfEventsPerServiceAccountUserPerHourOnAvg(),
                serviceAcountUserActivityRange);
    }


    private int getTimeIntervalForNonActiveRangeForServiceAccountUsers() {
        return 120000;
    }


    //abnormal behavior.

    private int getNumOfNormalUsersDailyForNonActiveWorkingHours() {
        return (int) getNumOfUsersDaily(getNumOfNormalUsers(), getPercentOfNormalUserWithAnomaliesPerDayOutOfTotalAmountOfUsers());
    }


    private double getEventProbabilityForNormalUsersForNonActiveWorkingHours() {
        return getEventProbability(getNumOfNormalUsersDailyForNonActiveWorkingHours(),
                getNumOfEventsPerNormalUserWithAnomaliesPerHourOnAvg(),
                normalUserAbnormalActivityRange);
    }


    private int getNumOfAdminUsersDailyForNonActiveWorkingHours() {
        return (int) getNumOfUsersDaily(getNumOfAdminUsers(), getPercentOfAdminUserWithAnomaliesPerDayOutOfTotalAmountOfUsers());
    }


    private double getEventProbabilityForAdminUsersForNonActiveWorkingHours() {
        return getEventProbability(getNumOfAdminUsersDailyForNonActiveWorkingHours(),
                getNumOfEventsPerAdminUserWithAnomaliesPerHourOnAvg(),
                adminUserAbnormalActivityRange);
    }


    private int getNumOfServiceAccountUsersDailyForNonActiveWorkingHours() {
        return (int) getNumOfUsersDaily(getNumOfServiceAccountUsers(), getPercentOfServiceAccountUserWithAnomaliesPerDayOutOfTotalAmountOfUsers());
    }


    private double getEventProbabilityForServiceAccountUsersForNonActiveWorkingHours() {
        return getEventProbability(getNumOfServiceAccountUsersDailyForNonActiveWorkingHours(),
                getNumOfEventsPerServiceAccountUserWithAnomaliesPerHourOnAvg(),
                serviceAcountUserActivityRange);
    }



    //==================================================================================
    // Creating Random Event Generators for all kind of users
    //==================================================================================

    protected abstract AbstractEventGenerator<Event> getNormalUserEventGenerator(IUserGenerator normalUsersDailyGenerator);

    private RandomMultiEventGenerator createNormalUsersRandomEventGenerator(Instant startInstant,
                                                                              Instant endInstant){
        IUserGenerator normalUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfNormalUsersDaily())), builderAllNormalUsers);
        return createRandomEventGenerator(getNormalUserEventGenerator(normalUsersDailyGenerator),
                normalUserActivityRange,
                getEventProbabilityForNormalUsers(),
                getTimeIntervalForNonActiveRangeForNormalUsers(),
                startInstant,
                endInstant
        );
    }

    protected abstract AbstractEventGenerator<Event> getNormalUsersAbnormalEventGenerator(IUserGenerator normalUsersDailyGenerator);

    private RandomMultiEventGenerator createNormalUsersRandomAbnormalEventGenerator(Instant startInstant,
                                                                                      Instant endInstant){
        IUserGenerator normalUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfNormalUsersDailyForNonActiveWorkingHours())), builderAllNormalUsers);
        return createRandomEventGenerator(getNormalUsersAbnormalEventGenerator(normalUsersDailyGenerator),
                normalUserAbnormalActivityRange,
                getEventProbabilityForNormalUsersForNonActiveWorkingHours(),
                getTimeIntervalForNonActiveRangeForNormalUsers(),
                startInstant,
                endInstant
        );
    }

    protected abstract AbstractEventGenerator<Event> getAdminUserEventGenerator(IUserGenerator adminUsersDailyGenerator);

    private RandomMultiEventGenerator createAdminUsersEventGenerator(Instant startInstant,
                                                                       Instant endInstant){
        IUserGenerator adminUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfAdminUsersDaily())), builderAllAdminUsers);
        return createRandomEventGenerator(getAdminUserEventGenerator(adminUsersDailyGenerator),
                adminUserActivityRange,
                getEventProbabilityForAdminUsers(),
                getTimeIntervalForNonActiveRangeForAdminUsers(),
                startInstant,
                endInstant
        );
    }

    protected abstract AbstractEventGenerator<Event> getAdminUsersAbnormalEventGenerator(IUserGenerator adminUsersDailyGenerator);

    private RandomMultiEventGenerator createAdminUsersAbnormalEventGenerator(Instant startInstant,
                                                                               Instant endInstant){
        IUserGenerator adminUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfAdminUsersDailyForNonActiveWorkingHours())), builderAllAdminUsers);
        return createRandomEventGenerator(getAdminUsersAbnormalEventGenerator(adminUsersDailyGenerator),
                adminUserAbnormalActivityRange,
                getEventProbabilityForAdminUsersForNonActiveWorkingHours(),
                getTimeIntervalForNonActiveRangeForAdminUsers(),
                startInstant,
                endInstant
        );
    }

    protected abstract AbstractEventGenerator<Event> getServiceAccountUserEventGenerator(IUserGenerator serviceAccountUsersDailyGenerator);

    private RandomMultiEventGenerator createServiceAccountUsersEventGenerator(Instant startInstant,
                                                                                Instant endInstant){
        IUserGenerator serviceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfServiceAccountUsersDaily())), builderAllServiceAccountUsers);
        return createRandomEventGenerator(getServiceAccountUserEventGenerator(serviceAccountUsersDailyGenerator),
                serviceAcountUserActivityRange,
                getEventProbabilityForServiceAccountUsers(),
                getTimeIntervalForNonActiveRangeForServiceAccountUsers(),
                startInstant,
                endInstant
        );
    }

    protected abstract AbstractEventGenerator<Event> getServiceAccountUsersAbnormalEventGenerator(IUserGenerator adminUsersDailyGenerator);

    private RandomMultiEventGenerator createServiceAccountUsersAbnormalEventGenerator(Instant startInstant,
                                                                                        Instant endInstant){
        IUserGenerator serviceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator((int)(Math.ceil(getNumOfServiceAccountUsersDailyForNonActiveWorkingHours())), builderAllServiceAccountUsers);
        return createRandomEventGenerator(getServiceAccountUsersAbnormalEventGenerator(serviceAccountUsersDailyGenerator),
                serviceAcountUserActivityRange,
                getEventProbabilityForServiceAccountUsersForNonActiveWorkingHours(),
                getTimeIntervalForNonActiveRangeForServiceAccountUsers(),
                startInstant,
                endInstant
        );
    }
}