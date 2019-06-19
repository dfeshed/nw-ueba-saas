package presidio.data.generators.event;

import presidio.data.domain.event.Event;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public abstract class UserOrientedEventGeneratorsBuilder extends EventGeneratorsBuilder {

    /** USERS **/
    protected IUserGenerator normalUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange;
    protected List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange;
    protected IUserGenerator adminUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange;
    protected List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange;
    protected IUserGenerator serviceAccountUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange;

    private double usersMultiplier;



    public UserOrientedEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                          List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                          List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                          IUserGenerator adminUserGenerator,
                                          List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                          List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                          IUserGenerator serviceAccountUserGenerator,
                                          List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange){
        this.normalUserGenerator = normalUserGenerator;
        this.normalUserActivityRange = normalUserActivityRange;
        this.normalUserAbnormalActivityRange = normalUserAbnormalActivityRange;
        this.adminUserGenerator = adminUserGenerator;
        this.adminUserActivityRange = adminUserActivityRange;
        this.adminUserAbnormalActivityRange = adminUserAbnormalActivityRange;
        this.serviceAccountUserGenerator = serviceAccountUserGenerator;
        this.serviceAcountUserActivityRange = serviceAcountUserActivityRange;

        this.usersMultiplier = 1;
    }

    public double getUsersMultiplier() {
        return usersMultiplier;
    }

    public void setUsersMultiplier(double usersMultiplier) {
        this.usersMultiplier = usersMultiplier;
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

    protected abstract RandomMultiEventGenerator createNormalUsersRandomEventGenerator(Instant startInstant,
                                                                                       Instant endInstant);
    protected abstract RandomMultiEventGenerator createNormalUsersRandomAbnormalEventGenerator(Instant startInstant,
                                                                                               Instant endInstant);
    protected abstract RandomMultiEventGenerator createAdminUsersEventGenerator(Instant startInstant,
                                                                                Instant endInstant);
    protected abstract RandomMultiEventGenerator createAdminUsersAbnormalEventGenerator(Instant startInstant,
                                                                                        Instant endInstant);
    protected abstract RandomMultiEventGenerator createServiceAccountUsersEventGenerator(Instant startInstant,
                                                                                         Instant endInstant);
    protected abstract RandomMultiEventGenerator createServiceAccountUsersAbnormalEventGenerator(Instant startInstant,
                                                                                                 Instant endInstant);
}