package presidio.integration.performance.scenario;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.MachineGeneratorDoubleWeightRouter;
import presidio.data.generators.machine.UserDesktopGenerator;
import presidio.data.generators.machine.UserServerGenerator;
import presidio.integration.performance.generators.authentication.FailureInteractiveAuthenticationUseCaseEventGeneratorsBuilder;
import presidio.integration.performance.generators.authentication.FailureRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder;
import presidio.integration.performance.generators.authentication.SuccessfulInteractiveAuthenticationUseCaseEventGeneratorsBuilder;
import presidio.integration.performance.generators.authentication.SuccessfulRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationPerformanceStabilityScenario extends UserOrientedPerformanceStabilityScenario {

    //machine entities pools
    private List<MachineEntity> globalServersMachineEntitiesPool;
    private List<MachineEntity> adminServersMachineEntitiesPool;
    private List<MachineEntity> localServersMachineEntitiesPool;



    public AuthenticationPerformanceStabilityScenario(Instant startInstant,
                                                      Instant endInstant,
                                                      int numOfNormalUsers, int numOfAdminUsers, int numOfserviceAccountUsers,
                                                      double probabilityMultiplier,
                                                      List<MachineEntity> globalServersMachineEntitiesPool,
                                                      List<MachineEntity> localServersMachineEntitiesPool,
                                                      List<MachineEntity> adminServersMachineEntitiesPool) {
        super(startInstant, endInstant, numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers,probabilityMultiplier);
        this.globalServersMachineEntitiesPool = globalServersMachineEntitiesPool;
        this.localServersMachineEntitiesPool = localServersMachineEntitiesPool;
        this.adminServersMachineEntitiesPool = adminServersMachineEntitiesPool;
    }

    @Override
    protected void initBuilders() throws GeneratorException {
        SuccessfulRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder successfulRemoteInteractiveEventGeneratorsBuilder =
                new SuccessfulRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        createSrcMachineGeneratorForNormalUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createSrcMachineGeneratorForAdminUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createSrcMachineGeneratorForServiceAccountUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForNormalUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForAdminUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForServiceAccountUser(),
                        createAbnormalMachineGeneratorForUser());

        eventGeneratorsBuilders.add(successfulRemoteInteractiveEventGeneratorsBuilder);

        FailureRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder failureRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder =
                new FailureRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        createSrcMachineGeneratorForNormalUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createSrcMachineGeneratorForAdminUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createSrcMachineGeneratorForServiceAccountUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForNormalUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForAdminUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForServiceAccountUser(),
                        createAbnormalMachineGeneratorForUser());

        eventGeneratorsBuilders.add(failureRemoteInteractiveAuthenticationUseCaseEventGeneratorsBuilder);

        SuccessfulInteractiveAuthenticationUseCaseEventGeneratorsBuilder successfulInteractiveEventGeneratorsBuilder =
                new SuccessfulInteractiveAuthenticationUseCaseEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        createSrcMachineGeneratorForNormalUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createSrcMachineGeneratorForAdminUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createSrcMachineGeneratorForServiceAccountUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForNormalUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForAdminUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForServiceAccountUser(),
                        createAbnormalMachineGeneratorForUser());

        eventGeneratorsBuilders.add(successfulInteractiveEventGeneratorsBuilder);

        FailureInteractiveAuthenticationUseCaseEventGeneratorsBuilder failureInteractiveAuthenticationUseCaseEventGeneratorsBuilder =
                new FailureInteractiveAuthenticationUseCaseEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        createSrcMachineGeneratorForNormalUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createSrcMachineGeneratorForAdminUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createSrcMachineGeneratorForServiceAccountUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForNormalUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForAdminUser(),
                        createAbnormalMachineGeneratorForUser(),
                        createDstMachineGeneratorForServiceAccountUser(),
                        createAbnormalMachineGeneratorForUser());

        eventGeneratorsBuilders.add(failureInteractiveAuthenticationUseCaseEventGeneratorsBuilder);
    }




    private List<MachineEntity> getGlobalServersMachineEntitiesPool(){
        return globalServersMachineEntitiesPool;
    }

    private List<MachineEntity> getAdminServersMachineEntitiesPool(){
        return adminServersMachineEntitiesPool;
    }

    private List<MachineEntity> getLocalServersMachineEntitiesPool(){
        return localServersMachineEntitiesPool;
    }

    private IMachineGenerator createSrcMachineGeneratorForNormalUser(){
        UserServerGenerator normalUserLocalServerGenerator =
                new UserServerGenerator(getLocalServersMachineEntitiesPool(),
                        getMinNumOfLocalServersPerNormalUser(),
                        getMaxNumOfLocalServersPerNormalUser());
        UserDesktopGenerator normalUserDesktopGenerator = new UserDesktopGenerator();

        List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs = new ArrayList<>();
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(normalUserLocalServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(normalUserDesktopGenerator, 99d));
        return new MachineGeneratorDoubleWeightRouter(machineGeneratorToFrequencyWeightPairs);
    }

    private IMachineGenerator createDstMachineGeneratorForNormalUser(){
        UserServerGenerator normalUserGlobalServerGenerator =
                new UserServerGenerator(getGlobalServersMachineEntitiesPool(),
                        getMinNumOfGlobalServersPerNormalUser(),
                        getMaxNumOfGlobalServersPerNormalUser());
        UserServerGenerator normalUserLocalServerGenerator =
                new UserServerGenerator(getLocalServersMachineEntitiesPool(),
                        getMinNumOfLocalServersPerNormalUser(),
                        getMaxNumOfLocalServersPerNormalUser());

        List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs = new ArrayList<>();
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(normalUserGlobalServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(normalUserLocalServerGenerator, 1d));
        return new MachineGeneratorDoubleWeightRouter(machineGeneratorToFrequencyWeightPairs);
    }

    private IMachineGenerator createSrcMachineGeneratorForAdminUser(){
        UserServerGenerator adminUserGlobalServerGenerator =
                new UserServerGenerator(getGlobalServersMachineEntitiesPool(),
                        getMinNumOfGlobalServersPerAdminUser(),
                        getMaxNumOfGlobalServersPerAdminUser());
        UserServerGenerator adminUserAdminServerGenerator =
                new UserServerGenerator(getAdminServersMachineEntitiesPool(),
                        getMinNumOfAdminServersPerAdminUser(),
                        getMaxNumOfAdminServersPerAdminUser());
        UserServerGenerator adminUserLocalServerGenerator =
                new UserServerGenerator(getLocalServersMachineEntitiesPool(),
                        getMinNumOfLocalServersPerAdminUser(),
                        getMaxNumOfLocalServersPerAdminUser());
        UserDesktopGenerator adminUserDesktopGenerator = new UserDesktopGenerator();

        List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs = new ArrayList<>();
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(adminUserGlobalServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(adminUserAdminServerGenerator, 10d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(adminUserLocalServerGenerator, 10d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(adminUserDesktopGenerator, 100d));
        return new MachineGeneratorDoubleWeightRouter(machineGeneratorToFrequencyWeightPairs);
    }

    private IMachineGenerator createDstMachineGeneratorForAdminUser(){
        UserServerGenerator adminUserGlobalServerGenerator =
                new UserServerGenerator(getGlobalServersMachineEntitiesPool(),
                        getMinNumOfGlobalServersPerAdminUser(),
                        getMaxNumOfGlobalServersPerAdminUser());
        UserServerGenerator adminUserAdminServerGenerator =
                new UserServerGenerator(getAdminServersMachineEntitiesPool(),
                        getMinNumOfAdminServersPerAdminUser(),
                        getMaxNumOfAdminServersPerAdminUser());
        UserServerGenerator adminUserLocalServerGenerator =
                new UserServerGenerator(getLocalServersMachineEntitiesPool(),
                        getMinNumOfLocalServersPerAdminUser(),
                        getMaxNumOfLocalServersPerAdminUser());
        UserDesktopGenerator adminUserDesktopGenerator = new UserDesktopGenerator();

        List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs = new ArrayList<>();
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(adminUserGlobalServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(adminUserAdminServerGenerator, 100d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(adminUserLocalServerGenerator, 100d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(adminUserDesktopGenerator, 1d));
        return new MachineGeneratorDoubleWeightRouter(machineGeneratorToFrequencyWeightPairs);
    }

    private IMachineGenerator createSrcMachineGeneratorForServiceAccountUser(){
        UserServerGenerator serviceAccountUserGlobalServerGenerator =
                new UserServerGenerator(getGlobalServersMachineEntitiesPool(),
                        getMinNumOfGlobalServersPerServiceAccountUser(),
                        getMaxNumOfGlobalServersPerServiceAccountUser());
        UserServerGenerator serviceAccountUserAdminServerGenerator =
                new UserServerGenerator(getAdminServersMachineEntitiesPool(),
                        getMinNumOfAdminServersPerServiceAccountUser(),
                        getMaxNumOfAdminServersPerServiceAccountUser());
        UserServerGenerator serviceAccountUserLocalServerGenerator =
                new UserServerGenerator(getLocalServersMachineEntitiesPool(),
                        getMinNumOfLocalServersPerServiceAccountUser(),
                        getMaxNumOfLocalServersPerServiceAccountUser());

        List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs = new ArrayList<>();
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(serviceAccountUserGlobalServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(serviceAccountUserAdminServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(serviceAccountUserLocalServerGenerator, 1d));
        return new MachineGeneratorDoubleWeightRouter(machineGeneratorToFrequencyWeightPairs);
    }

    private IMachineGenerator createDstMachineGeneratorForServiceAccountUser(){
        UserServerGenerator serviceAccountUserGlobalServerGenerator =
                new UserServerGenerator(getGlobalServersMachineEntitiesPool(),
                        getMinNumOfGlobalServersPerServiceAccountUser(),
                        getMaxNumOfGlobalServersPerServiceAccountUser());
        UserServerGenerator serviceAccountUserAdminServerGenerator =
                new UserServerGenerator(getAdminServersMachineEntitiesPool(),
                        getMinNumOfAdminServersPerServiceAccountUser(),
                        getMaxNumOfAdminServersPerServiceAccountUser());
        UserServerGenerator serviceAccountUserLocalServerGenerator =
                new UserServerGenerator(getLocalServersMachineEntitiesPool(),
                        getMinNumOfLocalServersPerServiceAccountUser(),
                        getMaxNumOfLocalServersPerServiceAccountUser());

        List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs = new ArrayList<>();
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(serviceAccountUserGlobalServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(serviceAccountUserAdminServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(serviceAccountUserLocalServerGenerator, 1d));
        return new MachineGeneratorDoubleWeightRouter(machineGeneratorToFrequencyWeightPairs);
    }

    private IMachineGenerator createAbnormalMachineGeneratorForUser(){
        UserServerGenerator userGlobalServerGenerator =
                new UserServerGenerator(getGlobalServersMachineEntitiesPool(),
                        getMinNumOfGlobalServersPerUserForNonActiveWorkingHours(),
                        getMaxNumOfGlobalServersPerUserForNonActiveWorkingHours());
        UserServerGenerator userAdminServerGenerator =
                new UserServerGenerator(getAdminServersMachineEntitiesPool(),
                        getMinNumOfAdminServersPerUserForNonActiveWorkingHours(),
                        getMaxNumOfAdminServersPerUserForNonActiveWorkingHours());
        UserServerGenerator userLocalServerGenerator =
                new UserServerGenerator(getLocalServersMachineEntitiesPool(),
                        getMinNumOfLocalServersPerUserForNonActiveWorkingHours(),
                        getMaxNumOfLocalServersPerUserForNonActiveWorkingHours());

        List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs = new ArrayList<>();
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(userGlobalServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(userAdminServerGenerator, 1d));
        machineGeneratorToFrequencyWeightPairs.add(new ImmutablePair<>(userLocalServerGenerator, 1d));
        return new MachineGeneratorDoubleWeightRouter(machineGeneratorToFrequencyWeightPairs);
    }


    private int getMinNumOfGlobalServersPerNormalUser(){
        return 2;
    }
    private int getMaxNumOfGlobalServersPerNormalUser(){
        return 10;
    }
    private int getMinNumOfLocalServersPerNormalUser(){
        return 1;
    }
    private int getMaxNumOfLocalServersPerNormalUser(){
        return 10;
    }


    private int getMinNumOfGlobalServersPerAdminUser(){
        return 4;
    }
    private int getMaxNumOfGlobalServersPerAdminUser(){
        return 50;
    }
    private int getMinNumOfLocalServersPerAdminUser(){
        return 10;
    }
    private int getMaxNumOfLocalServersPerAdminUser(){
        return 100;
    }
    private int getMinNumOfAdminServersPerAdminUser(){
        return 10;
    }
    private int getMaxNumOfAdminServersPerAdminUser(){
        return 100;
    }


    private int getMinNumOfGlobalServersPerServiceAccountUser(){
        return 1;
    }
    private int getMaxNumOfGlobalServersPerServiceAccountUser(){
        return 10;
    }
    private int getMinNumOfLocalServersPerServiceAccountUser(){
        return 1;
    }
    private int getMaxNumOfLocalServersPerServiceAccountUser(){
        return 10;
    }
    private int getMinNumOfAdminServersPerServiceAccountUser(){
        return 1;
    }
    private int getMaxNumOfAdminServersPerServiceAccountUser(){
        return 10;
    }

    private int getMinNumOfGlobalServersPerUserForNonActiveWorkingHours(){
        return 1;
    }
    private int getMaxNumOfGlobalServersPerUserForNonActiveWorkingHours(){
        return 2;
    }
    private int getMinNumOfLocalServersPerUserForNonActiveWorkingHours(){
        return 1;
    }
    private int getMaxNumOfLocalServersPerUserForNonActiveWorkingHours(){
        return 2;
    }
    private int getMinNumOfAdminServersPerUserForNonActiveWorkingHours(){
        return 1;
    }
    private int getMaxNumOfAdminServersPerUserForNonActiveWorkingHours(){
        return 2;
    }
}
