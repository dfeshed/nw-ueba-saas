package presidio.data.generators.event;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.NumberedUserRandomUniformallyGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public abstract class UserOrientedPerformanceStabilityScenario extends PerformanceStabilityScenario{

    protected double usersMultiplier;

    private final int ACTIVE_TIME_INTERVAL = 1000000; // nanos

    private final int NUM_OF_NORMAL_USERS = 94500;
    private final int NUM_OF_ADMIN_USERS = 5000;
    private final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;

    /** USERS **/
    protected IUserGenerator normalUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange;
    protected List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange;
    protected IUserGenerator adminUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange;
    protected List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange;
    protected IUserGenerator serviceAccountUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange;

    public UserOrientedPerformanceStabilityScenario(Instant startInstant, Instant endInstant,
                                                double probabilityMultiplier, double usersMultiplier) {
        super(startInstant, endInstant, probabilityMultiplier);
        this.usersMultiplier = usersMultiplier;
    }

    public void init() throws GeneratorException {
        normalUserGenerator = createNormalUserGenerator();
        normalUserActivityRange = getNormalUserActivityRange();
        normalUserAbnormalActivityRange = getNormalUserAbnormalActivityRange();
        adminUserGenerator = createAdminUserGenerator();
        adminUserActivityRange = getAdminUserActivityRange();
        adminUserAbnormalActivityRange = getAdminUserAbnormalActivityRange();
        serviceAccountUserGenerator = createServiceAccountUserGenerator();
        serviceAcountUserActivityRange = getServiceAcountUserActivityRange();

        super.init();
    }

    private IUserGenerator createNormalUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator((int)(Math.ceil(NUM_OF_NORMAL_USERS*usersMultiplier)),
                1, "normal_user_", "UID", false, false);
        return userGenerator;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getNormalUserActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(8,0), LocalTime.of(16,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getNormalUserAbnormalActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(0,0), LocalTime.of(8,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(16,0), LocalTime.of(23,59), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private IUserGenerator createAdminUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator((int)(Math.ceil(NUM_OF_ADMIN_USERS*usersMultiplier)), 1, "admin_user_", "UID", false, false);
        return userGenerator;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getAdminUserActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(6,0), LocalTime.of(22,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getAdminUserAbnormalActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(0,0), LocalTime.of(6,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(22,0), LocalTime.of(23,59), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private IUserGenerator createServiceAccountUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator((int)(Math.ceil(NUM_OF_SERVICE_ACCOUNT_USERS*usersMultiplier)), 1, "sa_user_", "UID", false, false);
        return userGenerator;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getServiceAcountUserActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(0,0), LocalTime.of(23,59), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }
}
