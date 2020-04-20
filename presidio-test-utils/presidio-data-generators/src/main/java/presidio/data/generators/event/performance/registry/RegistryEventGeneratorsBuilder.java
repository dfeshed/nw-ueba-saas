package presidio.data.generators.event.performance.registry;

import presidio.data.domain.FileEntity;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.*;
import presidio.data.generators.event.performance.EndpointEventGeneratorsBuilder;
import presidio.data.generators.event.process.CyclicOperationTypeGenerator;
import presidio.data.generators.event.registry.RegistryEventsGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessEntityGenerator;
import presidio.data.generators.registryentry.IRegistryEntryGenerator;
import presidio.data.generators.registryop.RegistryOperationGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;
import java.util.Map;


public abstract class RegistryEventGeneratorsBuilder extends EndpointEventGeneratorsBuilder {

    /** registry keys **/
    protected Map<String, List<String>> registryKeyGroupToRegistryKey;
    protected Map<String, List<String>> registryKeyToValueNamesMap;


    public RegistryEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                         IUserGenerator adminUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                         IUserGenerator serviceAccountUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                         IMachineGenerator machineGenerator,
                                         List<FileEntity> nonImportantProcesses,
                                          Map<String, List<String>> registryKeyGroupToRegistryKey,
                                          Map<String, List<String>> registryKeyToValueNamesMap){
        super(normalUserGenerator,
                normalUserActivityRange,
                normalUserAbnormalActivityRange,
                adminUserGenerator,
                adminUserActivityRange,
                adminUserAbnormalActivityRange,
                serviceAccountUserGenerator,
                serviceAcountUserActivityRange,
                machineGenerator,
                nonImportantProcesses);
        this.registryKeyGroupToRegistryKey = registryKeyGroupToRegistryKey;
        this.registryKeyToValueNamesMap = registryKeyToValueNamesMap;
    }

    protected void fillRegistryEventsGeneratorWithDefaultGenerators(RegistryEventsGenerator registryEventsGenerator,
                                                                    ProcessEntityGenerator processEntityGenerator,
                                                                    IRegistryEntryGenerator registryEntityGenerator,
                                                                    String[] operationTypeNames,
                                                                   String testCase) {
        //operator generator
        RegistryOperationGenerator opGenerator = new RegistryOperationGenerator();

        presidio.data.generators.event.process.CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(operationTypeNames);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        opGenerator.setProcessEntityGenerator(processEntityGenerator);
        opGenerator.setRegistryEntryGenerator(registryEntityGenerator);
        registryEventsGenerator.setRegistryOperationGenerator(opGenerator);
        //event id generator
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testCase);
        registryEventsGenerator.setEventIdGenerator(eventIdGen);
    }
}
