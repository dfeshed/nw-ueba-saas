package presidio.data.generators.event.registry;

import presidio.data.domain.FileEntity;
import presidio.data.domain.event.registry.REGISTRY_OPERATION_TYPE;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;
import java.util.Map;

public class GeneralRegistryUseCaseEventGeneratorsBuilder extends RegistryUseCaseEventGeneratorsBuilder{


    public GeneralRegistryUseCaseEventGeneratorsBuilder(IUserGenerator normalUserGenerator, List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange, List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange, IUserGenerator adminUserGenerator, List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange, List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange, IUserGenerator serviceAccountUserGenerator, List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange, IMachineGenerator machineGenerator, List<FileEntity> nonImportantProcesses, Map<String, List<String>> registryKeyGroupToRegistryKey, Map<String, List<String>> registryKeyToValueNamesMap) {
        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange, adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange, serviceAccountUserGenerator, serviceAcountUserActivityRange, machineGenerator, nonImportantProcesses, registryKeyGroupToRegistryKey, registryKeyToValueNamesMap);
    }

    @Override
    protected int getNumOfNormalUsers() {
        return 94500;
    }

    @Override
    protected int getNumOfNormalUsersDaily() {
        return 70000;
    }

    @Override
    protected double getEventProbabilityForNormalUsers() {
        return 0.49;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForNormalUsers() {
        return 120000;
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
    protected int getNumOfAdminUsers() {
        return 5000;
    }

    @Override
    protected int getNumOfAdminUsersDaily() {
        return 4900;
    }

    @Override
    protected double getEventProbabilityForAdminUsers() {
        return 0.3;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForAdminUsers() {
        return 120000;
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
    protected int getNumOfServiceAccountUsers() {
        return 500;
    }

    @Override
    protected int getNumOfServiceAccountUsersDaily() {
        return 500;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsers() {
        return 0.0025;
    }

    @Override
    protected int getTimeIntervalForNonActiveRangeForServiceAccountUsers() {
        return 120000;
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
    protected int getNumOfNormalUsersDailyForNonActiveWorkingHours() {
        return 180;
    }

    @Override
    protected double getEventProbabilityForNormalUsersForNonActiveWorkingHours() {
        return 0.00081;
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
    protected int getNumOfAdminUsersDailyForNonActiveWorkingHours() {
        return 10;
    }

    @Override
    protected double getEventProbabilityForAdminUsersForNonActiveWorkingHours() {
        return 0.001;
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
    protected int getNumOfServiceAccountUsersDailyForAbnormalEvents() {
        return 2;
    }

    @Override
    protected double getEventProbabilityForServiceAccountUsersForAbnormalEvents() {
        return 0.0005;
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
        return null;
    }

    @Override
    protected IStringListGenerator getProcessDirectoryGroupsGenerator(IFileEntityGenerator fileEntityGenerator) {
        return null;
    }
}
