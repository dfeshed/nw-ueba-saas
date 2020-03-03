package presidio.integration.performance.utils;

import fortscale.common.general.Schema;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.RandomMultiMachineEntityGenerator;
import presidio.integration.performance.scenario.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PerformanceScenariosSupplier implements Supplier<List<PerformanceScenario>> {

    private final Schema schema;
    private final TestProperties test;
    private final int intervalHours;

    public PerformanceScenariosSupplier(Schema schema, TestProperties test, int intervalHours) {
        this.schema = schema;
        this.test = test;
        this.intervalHours = intervalHours;
    }

    @Override
    public List<PerformanceScenario> get() {
        return split();
    }

    private List<PerformanceScenario> split() {
        int hours = (int) Duration.between(test.startInstant, test.endInstant).abs().toHours();
        int intervals;

        if (intervalHours > 0) {
            intervals = hours / intervalHours;
        } else {
            intervals = 0;
        }

        List<PerformanceScenario> splitted = IntStream.range(0, intervals).mapToObj(i -> {
            Instant start = test.startInstant.plus(i * intervalHours, ChronoUnit.HOURS);
            Instant end = start.plus(intervalHours, ChronoUnit.HOURS).minusSeconds(1);
            return getScenario(start, end);
        }).collect(Collectors.toList());

        Instant start = test.startInstant.plus(intervals * intervalHours, ChronoUnit.HOURS);
        if (test.endInstant.isAfter(start)) {
            splitted.add(getScenario(start, test.endInstant));
        }
        return splitted;
    }


    private PerformanceScenario getScenario(Instant from, Instant to) {
        double usersMultiplier = test.usersMultiplier;
        double probabilityMultiplier = test.usersProbabilityMultiplier;

        if (schema.equals(Schema.TLS)) {
            TlsPerformanceStabilityScenario scenario = new TlsPerformanceStabilityScenario(from, to,
                    test.tlsAlertsProbability, test.tlsGroupsToCreate, test.tlsEventsPerDayPerGroup);
            return new PerformanceScenario(from, to, schema, scenario);
        }


        if (schema.equals(Schema.PROCESS)) {
            final int numOfNormalUsers = (int) (test.PROCESS_NUM_OF_NORMAL_USERS * usersMultiplier);
            final int numOfAdminUsers = (int) (test.PROCESS_NUM_OF_ADMIN_USERS * usersMultiplier);
            final int numOfServiceAccountUsers = (int) (test.PROCESS_NUM_OF_SERVICE_ACCOUNT_USERS * usersMultiplier);

            ProcessPerformanceStabilityScenario scenario = new ProcessPerformanceStabilityScenario(from, to,
                    numOfNormalUsers, numOfAdminUsers, numOfServiceAccountUsers, probabilityMultiplier);

            return new PerformanceScenario(from, to, schema, scenario);
        }

        if (schema.equals(Schema.REGISTRY)) {
            final int numOfNormalUsers = (int) (test.REGISTRY_NUM_OF_NORMAL_USERS * usersMultiplier);
            final int numOfAdminUsers = (int) (test.REGISTRY_NUM_OF_ADMIN_USERS * usersMultiplier);
            final int numOfServiceAccountUsers = (int) (test.REGISTRY_NUM_OF_SERVICE_ACCOUNT_USERS * usersMultiplier);
            RegistryPerformanceStabilityScenario scenario = new RegistryPerformanceStabilityScenario(
                            from, to,
                            numOfNormalUsers, numOfAdminUsers, numOfServiceAccountUsers, probabilityMultiplier);

            return new PerformanceScenario(from, to, schema, scenario);
        }

        if (schema.equals(Schema.AUTHENTICATION)) {
            final int numOfNormalUsers = (int) (test.AUTHENTICATION_NUM_OF_NORMAL_USERS * usersMultiplier);
            final int numOfAdminUsers = (int) (test.AUTHENTICATION_NUM_OF_ADMIN_USERS * usersMultiplier);
            final int numOfServiceAccountUsers = (int) (test.AUTHENTICATION_NUM_OF_SERVICE_ACCOUNT_USERS * usersMultiplier);
            AuthenticationPerformanceStabilityScenario scenario = new AuthenticationPerformanceStabilityScenario(
                            from, to, numOfNormalUsers, numOfAdminUsers, numOfServiceAccountUsers, probabilityMultiplier,
                            createGlobalServerMachinePool(),
                            createLocalServerMachinePool(),
                            createAdminServerMachinePool());

            return new PerformanceScenario(from, to, schema, scenario);
        }

        if (schema.equals(Schema.ACTIVE_DIRECTORY)) {
            final int numOfNormalUsers = (int) (test.ACTIVE_DIRECTORY_NUM_OF_NORMAL_USERS * usersMultiplier);
            final int numOfAdminUsers = (int) (test.ACTIVE_DIRECTORY_NUM_OF_ADMIN_USERS * usersMultiplier);
            final int numOfServiceAccountUsers = (int) (test.ACTIVE_DIRECTORY_NUM_OF_SERVICE_ACCOUNT_USERS * usersMultiplier);
            ActiveDirectoryPerformanceStabilityScenario scenario = new ActiveDirectoryPerformanceStabilityScenario(from, to,
                    numOfNormalUsers, numOfAdminUsers, numOfServiceAccountUsers, probabilityMultiplier);

            return new PerformanceScenario(from, to, schema, scenario);
        }

        if (schema.equals(Schema.FILE)) {
            final int numOfNormalUsers = (int) (test.FILE_NUM_OF_NORMAL_USERS * usersMultiplier);
            final int numOfAdminUsers = (int) (test.FILE_NUM_OF_ADMIN_USERS * usersMultiplier);
            final int numOfServiceAccountUsers = (int) (test.FILE_NUM_OF_SERVICE_ACCOUNT_USERS * usersMultiplier);
            FilePerformanceStabilityScenario scenario = new FilePerformanceStabilityScenario(
                    from, to, numOfNormalUsers, numOfAdminUsers, numOfServiceAccountUsers, probabilityMultiplier,
                    createGlobalServerMachinePool(),
                    createLocalServerMachinePool(),
                    createAdminServerMachinePool());

            return new PerformanceScenario(from, to, schema, scenario);
        }

        throw new RuntimeException("No such schema " + schema);
    }


    private List<MachineEntity> createGlobalServerMachinePool() {
        return createNonDesktopMachinePool(
                test.NUM_OF_GLOBAL_SERVER_MACHINES_CLUSTERS,
                test.GLOBAL_SERVER_MACHINES_CLUSTER_PREFIX,
                test.NUM_OF_GLOBAL_SERVER_MACHINES_PER_CLUSTER,
                test.NUM_OF_GLOBAL_SERVER_MACHINES);
    }

    private List<MachineEntity> createAdminServerMachinePool() {
        return createNonDesktopMachinePool(
                test.NUM_OF_ADMIN_SERVER_MACHINES_CLUSTERS,
                test.ADMIN_SERVER_MACHINES_CLUSTER_PREFIX,
                test.NUM_OF_ADMIN_SERVER_MACHINES_PER_CLUSTER,
                test.NUM_OF_ADMIN_SERVER_MACHINES);
    }

    private List<MachineEntity> createLocalServerMachinePool() {
        return createNonDesktopMachinePool(
                test.NUM_OF_LOCAL_SERVER_MACHINES_CLUSTERS,
                test.LOCAL_SERVER_MACHINES_CLUSTER_PREFIX,
                test.NUM_OF_LOCAL_SERVER_MACHINES_PER_CLUSTER,
                test.NUM_OF_LOCAL_SERVER_MACHINES);
    }

    private List<MachineEntity> createNonDesktopMachinePool(final int numOfClusters, final String clusterPrefix,
                                                            final int numOfMachinesPerCluster, final int numOfMachines) {
        IMachineGenerator generator =
                new RandomMultiMachineEntityGenerator(
                        Arrays.asList("domain1"),
                        numOfClusters, clusterPrefix,
                        numOfMachinesPerCluster, "");
        Map<String, MachineEntity> nameToMachineEntityMap = new HashMap<>();
        while (nameToMachineEntityMap.size() < numOfMachines) {
            MachineEntity machineEntity = generator.getNext();
            nameToMachineEntityMap.put(machineEntity.getMachineId(), machineEntity);
        }

        return new ArrayList<>(nameToMachineEntityMap.values());
    }


}
