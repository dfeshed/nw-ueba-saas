package presidio.data.generators.event.performance.scenario;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.FileEntity;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.performance.file.FailureFileActionEventGeneratorsBuilder;
import presidio.data.generators.event.performance.file.FailureFilePermissionEventGeneratorsBuilder;
import presidio.data.generators.event.performance.file.SuccessfulFileActionEventGeneratorsBuilder;
import presidio.data.generators.event.performance.file.SuccessfulFilePermissionEventGeneratorsBuilder;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.RandomFileEntityGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.MachineGeneratorDoubleWeightRouter;
import presidio.data.generators.machine.UserDesktopGenerator;
import presidio.data.generators.machine.UserServerGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilePerformanceStabilityScenario extends UserOrientedPerformanceStabilityScenario {

    //machine entities pools
    private List<MachineEntity> globalServersMachineEntitiesPool;
    private List<MachineEntity> adminServersMachineEntitiesPool;
    private List<MachineEntity> localServersMachineEntitiesPool;

    //file entities pool
    private List<FileEntity> fileEntitiesPool;



    public FilePerformanceStabilityScenario(Instant startInstant,
                                            Instant endInstant,
                                            double probabilityMultiplier,
                                            double usersMultiplier,
                                            List<MachineEntity> globalServersMachineEntitiesPool,
                                            List<MachineEntity> localServersMachineEntitiesPool,
                                            List<MachineEntity> adminServersMachineEntitiesPool) {
        super(startInstant, endInstant, probabilityMultiplier, usersMultiplier);
        this.globalServersMachineEntitiesPool = globalServersMachineEntitiesPool;
        this.localServersMachineEntitiesPool = localServersMachineEntitiesPool;
        this.adminServersMachineEntitiesPool = adminServersMachineEntitiesPool;
        this.fileEntitiesPool = generateFileEntities();
    }

    @Override
    protected void initBuilders() throws GeneratorException {
        IFileEntityGenerator normalUserSrcFileEntitiesGenerator = createSrcFileEntitiesGeneratorForNormalUser();
        IFileEntityGenerator normalUserAbnormalSrcFileEntitiesGenerator = createAbnormalFileEntitiesGeneratorForUser();
        IFileEntityGenerator adminUserSrcFileEntitiesGenerator = createSrcFileEntitiesGeneratorForAdminUser();
        IFileEntityGenerator adminUserAbnormalSrcFileEntitiesGenerator = createAbnormalFileEntitiesGeneratorForUser();
        IFileEntityGenerator serviceAccountUserSrcFileEntitiesGenerator = createSrcFileEntitiesGeneratorForServiceAccountUser();
        IFileEntityGenerator serviceAccountUserAbnormalSrcFileEntitiesGenerator = createAbnormalFileEntitiesGeneratorForUser();
        IFileEntityGenerator normalUserDstFileEntitiesGenerator = createDstFileEntitiesGeneratorForNormalUser();
        IFileEntityGenerator normalUserAbnormalDstFileEntitiesGenerator = createAbnormalFileEntitiesGeneratorForUser();
        IFileEntityGenerator adminUserDstFileEntitiesGenerator = createDstFileEntitiesGeneratorForAdminUser();
        IFileEntityGenerator adminUserAbnormalDstFileEntitiesGenerator = createAbnormalFileEntitiesGeneratorForUser();
        IFileEntityGenerator serviceAccountUserDstFileEntitiesGenerator = createDstFileEntitiesGeneratorForServiceAccountUser();
        IFileEntityGenerator serviceAccountUserAbnormalDstFileEntitiesGenerator = createAbnormalFileEntitiesGeneratorForUser();
        SuccessfulFilePermissionEventGeneratorsBuilder successfulFilePermissionEventGeneratorsBuilder =
                new SuccessfulFilePermissionEventGeneratorsBuilder(
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
                        normalUserSrcFileEntitiesGenerator,
                        normalUserAbnormalSrcFileEntitiesGenerator,
                        adminUserSrcFileEntitiesGenerator,
                        adminUserAbnormalSrcFileEntitiesGenerator,
                        serviceAccountUserSrcFileEntitiesGenerator,
                        serviceAccountUserAbnormalSrcFileEntitiesGenerator,
                        normalUserDstFileEntitiesGenerator,
                        normalUserAbnormalDstFileEntitiesGenerator,
                        adminUserDstFileEntitiesGenerator,
                        adminUserAbnormalDstFileEntitiesGenerator,
                        serviceAccountUserDstFileEntitiesGenerator,
                        serviceAccountUserAbnormalDstFileEntitiesGenerator);

        eventGeneratorsBuilders.add(successfulFilePermissionEventGeneratorsBuilder);

        FailureFilePermissionEventGeneratorsBuilder failureFilePermissionEventGeneratorsBuilder =
                new FailureFilePermissionEventGeneratorsBuilder(
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
                        normalUserSrcFileEntitiesGenerator,
                        normalUserAbnormalSrcFileEntitiesGenerator,
                        adminUserSrcFileEntitiesGenerator,
                        adminUserAbnormalSrcFileEntitiesGenerator,
                        serviceAccountUserSrcFileEntitiesGenerator,
                        serviceAccountUserAbnormalSrcFileEntitiesGenerator,
                        normalUserDstFileEntitiesGenerator,
                        normalUserAbnormalDstFileEntitiesGenerator,
                        adminUserDstFileEntitiesGenerator,
                        adminUserAbnormalDstFileEntitiesGenerator,
                        serviceAccountUserDstFileEntitiesGenerator,
                        serviceAccountUserAbnormalDstFileEntitiesGenerator);

        eventGeneratorsBuilders.add(failureFilePermissionEventGeneratorsBuilder);

        SuccessfulFileActionEventGeneratorsBuilder successfulFileActionEventGeneratorsBuilder =
                new SuccessfulFileActionEventGeneratorsBuilder(
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
                        normalUserSrcFileEntitiesGenerator,
                        normalUserAbnormalSrcFileEntitiesGenerator,
                        adminUserSrcFileEntitiesGenerator,
                        adminUserAbnormalSrcFileEntitiesGenerator,
                        serviceAccountUserSrcFileEntitiesGenerator,
                        serviceAccountUserAbnormalSrcFileEntitiesGenerator,
                        normalUserDstFileEntitiesGenerator,
                        normalUserAbnormalDstFileEntitiesGenerator,
                        adminUserDstFileEntitiesGenerator,
                        adminUserAbnormalDstFileEntitiesGenerator,
                        serviceAccountUserDstFileEntitiesGenerator,
                        serviceAccountUserAbnormalDstFileEntitiesGenerator);

        eventGeneratorsBuilders.add(successfulFileActionEventGeneratorsBuilder);

        FailureFileActionEventGeneratorsBuilder failureFileActionEventGeneratorsBuilder =
                new FailureFileActionEventGeneratorsBuilder(
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
                        normalUserSrcFileEntitiesGenerator,
                        normalUserAbnormalSrcFileEntitiesGenerator,
                        adminUserSrcFileEntitiesGenerator,
                        adminUserAbnormalSrcFileEntitiesGenerator,
                        serviceAccountUserSrcFileEntitiesGenerator,
                        serviceAccountUserAbnormalSrcFileEntitiesGenerator,
                        normalUserDstFileEntitiesGenerator,
                        normalUserAbnormalDstFileEntitiesGenerator,
                        adminUserDstFileEntitiesGenerator,
                        adminUserAbnormalDstFileEntitiesGenerator,
                        serviceAccountUserDstFileEntitiesGenerator,
                        serviceAccountUserAbnormalDstFileEntitiesGenerator);

        eventGeneratorsBuilders.add(failureFileActionEventGeneratorsBuilder);
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
        return 10;
    }
    private int getMinNumOfLocalServersPerUserForNonActiveWorkingHours(){
        return 1;
    }
    private int getMaxNumOfLocalServersPerUserForNonActiveWorkingHours(){
        return 10;
    }
    private int getMinNumOfAdminServersPerUserForNonActiveWorkingHours(){
        return 1;
    }
    private int getMaxNumOfAdminServersPerUserForNonActiveWorkingHours(){
        return 10;
    }




    private IFileEntityGenerator createSrcFileEntitiesGeneratorForNormalUser(){
        return new UserFileEntityGenerator(
                fileEntitiesPool,
                getMinNumOfFileEntitiesPerNormalUser(),
                getMaxNumOfFileEntitiesPerNormalUser());
    }

    private IFileEntityGenerator createSrcFileEntitiesGeneratorForAdminUser(){
        return new UserFileEntityGenerator(
                fileEntitiesPool,
                getMinNumOfFileEntitiesPerAdminUser(),
                getMaxNumOfFileEntitiesPerAdminUser());
    }

    private IFileEntityGenerator createSrcFileEntitiesGeneratorForServiceAccountUser(){
        return new UserFileEntityGenerator(
                fileEntitiesPool,
                getMinNumOfFileEntitiesPerServiceAccountUser(),
                getMaxNumOfFileEntitiesPerServiceAccountUser());
    }

    private IFileEntityGenerator createDstFileEntitiesGeneratorForNormalUser(){
        return new UserFileEntityGenerator(
                fileEntitiesPool,
                getMinNumOfFileEntitiesPerNormalUser(),
                getMaxNumOfFileEntitiesPerNormalUser());
    }

    private IFileEntityGenerator createDstFileEntitiesGeneratorForAdminUser(){
        return new UserFileEntityGenerator(
                fileEntitiesPool,
                getMinNumOfFileEntitiesPerAdminUser(),
                getMaxNumOfFileEntitiesPerAdminUser());
    }

    private IFileEntityGenerator createDstFileEntitiesGeneratorForServiceAccountUser(){
        return new UserFileEntityGenerator(
                fileEntitiesPool,
                getMinNumOfFileEntitiesPerServiceAccountUser(),
                getMaxNumOfFileEntitiesPerServiceAccountUser());
    }

    private IFileEntityGenerator createAbnormalFileEntitiesGeneratorForUser(){
        return new UserFileEntityGenerator(
                fileEntitiesPool,
                getMinNumOfFileEntitiesPerUserForNonActiveWorkingHours(),
                getMaxNumOfFileEntitiesPerUserForNonActiveWorkingHours());
    }




    private int getMinNumOfFileEntitiesPerNormalUser(){
        return 10;
    }
    private int getMaxNumOfFileEntitiesPerNormalUser(){
        return 100;
    }


    private int getMinNumOfFileEntitiesPerAdminUser(){
        return 50;
    }
    private int getMaxNumOfFileEntitiesPerAdminUser(){
        return 500;
    }


    private int getMinNumOfFileEntitiesPerServiceAccountUser(){
        return 1;
    }
    private int getMaxNumOfFileEntitiesPerServiceAccountUser(){
        return 10;
    }


    private int getMinNumOfFileEntitiesPerUserForNonActiveWorkingHours(){
        return 1;
    }
    private int getMaxNumOfFileEntitiesPerUserForNonActiveWorkingHours(){
        return 2000;
    }

    private List<FileEntity> generateFileEntities(){
        RandomFileEntityGenerator randomFileEntityGenerator =
                new RandomFileEntityGenerator(1000, "dir", "",
                        10000, "test", ".pdf");
        Set<FileEntity> fileEntitySet = new HashSet<>();
        while(fileEntitySet.size() < 10000){
            fileEntitySet.add(randomFileEntityGenerator.getNext());
        }
        return new ArrayList<>(fileEntitySet);
    }
}
