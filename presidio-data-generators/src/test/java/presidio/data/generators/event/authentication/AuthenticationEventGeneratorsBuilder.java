package presidio.data.generators.event.authentication;

import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.EventGeneratorsBuilder;
import presidio.data.generators.event.UserOrientedEventGeneratorsBuilder;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;

public abstract class AuthenticationEventGeneratorsBuilder extends UserOrientedEventGeneratorsBuilder {


//    //Normal behavior + abnormal time.
//    protected abstract long getNumOfNormalUsers();
//    protected abstract long getNumOfNormalUsersDaily();
//    protected abstract double getEventProbabilityForNormalUsers();
//    protected abstract int getTimeIntervalForNonActiveRangeForNormalUsers();
//
//
//    protected abstract int getNumOfAdminUsers();
//    protected abstract int getNumOfAdminUsersDaily();
//    protected abstract double getEventProbabilityForAdminUsers();
//    protected abstract int getTimeIntervalForNonActiveRangeForAdminUsers();
//
//    protected abstract int getNumOfServiceAccountUsers();
//    protected abstract int getNumOfServiceAccountUsersDaily();
//    protected abstract double getEventProbabilityForServiceAccountUsers();
//    protected abstract int getTimeIntervalForNonActiveRangeForServiceAccountUsers();
//
//    //abnormal behavior.
//    protected abstract int getNumOfNormalUsersDailyForNonActiveWorkingHours();
//    protected abstract double getEventProbabilityForNormalUsersForNonActiveWorkingHours();
//
//    protected abstract int getNumOfAdminUsersDailyForNonActiveWorkingHours();
//    protected abstract double getEventProbabilityForAdminUsersForNonActiveWorkingHours();
//
//    protected abstract int getNumOfServiceAccountUsersDailyForNonActiveWorkingHours();
//    protected abstract double getEventProbabilityForServiceAccountUsersForNonActiveWorkingHours();

    public AuthenticationEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                                List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                                List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                                IUserGenerator adminUserGenerator,
                                                List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                                List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                                IUserGenerator serviceAccountUserGenerator,
                                                List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange){
        super(normalUserGenerator,
                normalUserActivityRange,
                normalUserAbnormalActivityRange,
                adminUserGenerator,
                adminUserActivityRange,
                adminUserAbnormalActivityRange,
                serviceAccountUserGenerator,
                serviceAcountUserActivityRange);
    }
}
