package presidio.data.generators.event.authentication;

import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.EventGeneratorsBuilder;
import presidio.data.generators.event.UserOrientedEventGeneratorsBuilder;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;

public abstract class AuthenticationEventGeneratorsBuilder extends UserOrientedEventGeneratorsBuilder {



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
