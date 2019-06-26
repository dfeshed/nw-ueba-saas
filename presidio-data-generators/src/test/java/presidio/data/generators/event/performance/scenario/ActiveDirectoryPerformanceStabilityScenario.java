package presidio.data.generators.event.performance.scenario;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.performance.active_directory.FailureActiveDirectoryUseCaseEventGeneratorsBuilder;
import presidio.data.generators.event.performance.active_directory.SuccessfulActiveDirectoryUseCaseEventGeneratorsBuilder;

import java.time.Instant;


public class ActiveDirectoryPerformanceStabilityScenario extends UserOrientedPerformanceStabilityScenario {






    public ActiveDirectoryPerformanceStabilityScenario(Instant startInstant,
                                            Instant endInstant,
                                            double probabilityMultiplier,
                                            double usersMultiplier) {
        super(startInstant, endInstant, probabilityMultiplier, usersMultiplier);
    }

    @Override
    protected void initBuilders() throws GeneratorException {

        SuccessfulActiveDirectoryUseCaseEventGeneratorsBuilder successfulActiveDirectoryUseCaseEventGeneratorsBuilder =
                new SuccessfulActiveDirectoryUseCaseEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange);

        eventGeneratorsBuilders.add(successfulActiveDirectoryUseCaseEventGeneratorsBuilder);

        FailureActiveDirectoryUseCaseEventGeneratorsBuilder failureActiveDirectoryUseCaseEventGeneratorsBuilder =
                new FailureActiveDirectoryUseCaseEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange);

        eventGeneratorsBuilders.add(failureActiveDirectoryUseCaseEventGeneratorsBuilder);
    }
}
