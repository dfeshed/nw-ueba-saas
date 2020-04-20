package presidio.data.generators.event.performance.registry;

import presidio.data.domain.FileEntity;
import presidio.data.domain.event.registry.REGISTRY_OPERATION_TYPE;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessCategoriesGenerator;
import presidio.data.generators.processentity.ProcessDirectoryGroupsGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;
import java.util.Map;

public class GeneralRegistryUseCaseEventGeneratorsBuilder extends RegistryUseCaseEventGeneratorsBuilder{


    public GeneralRegistryUseCaseEventGeneratorsBuilder(IUserGenerator normalUserGenerator, List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange, List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange, IUserGenerator adminUserGenerator, List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange, List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange, IUserGenerator serviceAccountUserGenerator, List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange, IMachineGenerator machineGenerator, List<FileEntity> nonImportantProcesses, Map<String, List<String>> registryKeyGroupToRegistryKey, Map<String, List<String>> registryKeyToValueNamesMap) {
        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange, adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange, serviceAccountUserGenerator, serviceAcountUserActivityRange, machineGenerator, nonImportantProcesses, registryKeyGroupToRegistryKey, registryKeyToValueNamesMap);
    }

    @Override
    protected double getPercentOfNormalUserPerDayOutOfTotalAmountOfUsers() {
        return 0.75;
    }

    @Override
    protected double getNumOfEventsPerNormalUserPerHourOnAvg() {
        return 25;
    }

    @Override
    protected double getPercentOfAdminUserPerDayOutOfTotalAmountOfUsers() {
        return 0.99;
    }

    @Override
    protected double getNumOfEventsPerAdminUserPerHourOnAvg() {
        return 100;
    }

    @Override
    protected double getPercentOfServiceAccountUserPerDayOutOfTotalAmountOfUsers() {
        return 1;
    }

    @Override
    protected double getNumOfEventsPerServiceAccountUserPerHourOnAvg() {
        return 500;
    }

    @Override
    protected double getPercentOfNormalUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.002d;
    }

    @Override
    protected double getNumOfEventsPerNormalUserWithAnomaliesPerHourOnAvg() {
        return 100;
    }

    @Override
    protected double getPercentOfAdminUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.002d;
    }

    @Override
    protected double getNumOfEventsPerAdminUserWithAnomaliesPerHourOnAvg() {
        return 400;
    }

    @Override
    protected double getPercentOfServiceAccountUserWithAnomaliesPerDayOutOfTotalAmountOfUsers() {
        return 0.002d;
    }

    @Override
    protected double getNumOfEventsPerServiceAccountUserWithAnomaliesPerHourOnAvg() {
        return 2000;
    }



    @Override
    protected int getMinNumOfFilesPerNormalUserForProcesses() {
        return 100;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForProcesses() {
        return 200;
    }



    @Override
    protected int getMinNumOfFilesPerAdminUserForProcesses() {
        return 100;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForProcesses() {
        return 1000;
    }



    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForProcesses() {
        return 1;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForProcesses() {
        return 200;
    }

    @Override
    protected int getMinNumOfRegistryGroupsPerProcess() {
        return 1;
    }

    @Override
    protected int getMaxNumOfRegistryGroupsPerProcess() {
        return 5;
    }

    @Override
    protected List<FileEntity> getProcesses() {
        return nonImportantProcesses;
    }

    @Override
    protected String[] getOperationTypeNames() {
        String[] operationTypeNames = {REGISTRY_OPERATION_TYPE.SET_VALUE.value};
        return operationTypeNames;
    }

    @Override
    protected String getUseCaseTestName() {
        return "nonImportantToRegistryKeyGroup";
    }



    @Override
    protected int getMinNumOfFilesPerNormalUserForProcessesForAbnormalEvents() {
        return 100;
    }

    @Override
    protected int getMaxNumOfFilesPerNormalUserForProcessesForAbnormalEvents() {
        return 200;
    }



    @Override
    protected int getMinNumOfFilesPerAdminUserForProcessesForAbnormalEvents() {
        return 100;
    }

    @Override
    protected int getMaxNumOfFilesPerAdminUserForProcessesForAbnormalEvents() {
        return 1000;
    }



    @Override
    protected int getMinNumOfFilesPerServiceAccountUserForProcessesForAbnormalEvents() {
        return 5;
    }

    @Override
    protected int getMaxNumOfFilesPerServiceAccountUserForProcessesForAbnormalEvents() {
        return 200;
    }

    @Override
    protected IStringListGenerator getProcessCategoriesGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessCategoriesGenerator((List<String>) null);
    }

    @Override
    protected IStringListGenerator getProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator) {
        return new ProcessDirectoryGroupsGenerator((List<String>) null);
    }
}
