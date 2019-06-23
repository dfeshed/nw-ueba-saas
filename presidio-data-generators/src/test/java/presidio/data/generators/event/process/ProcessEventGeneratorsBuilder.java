package presidio.data.generators.event.process;

import presidio.data.domain.FileEntity;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.EndpointEventGeneratorsBuilder;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessEntityGenerator;
import presidio.data.generators.processop.ProcessOperationGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;

public abstract class ProcessEventGeneratorsBuilder extends EndpointEventGeneratorsBuilder {

    public ProcessEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                         IUserGenerator adminUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                         IUserGenerator serviceAccountUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                         IMachineGenerator machineGenerator,
                                         List<FileEntity> nonImportantProcesses){
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
    }

    protected void fillProcessEventsGeneratorWithDefaultGenerators(ProcessEventsGenerator processEventsGenerator,
                                                                 ProcessEntityGenerator srcProcessEntityGenerator,
                                                                 ProcessEntityGenerator dstProcessEntityGenerator,
                                                                 String[] operationTypeNames,
                                                                 String testCase){
        //operator generator
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();

        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(operationTypeNames);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        opGenerator.setSourceProcessEntityGenerator(srcProcessEntityGenerator);
        opGenerator.setDestProcessEntityGenerator(dstProcessEntityGenerator);
        processEventsGenerator.setProcessOperationGenerator(opGenerator);
        //event id generator
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testCase);
        processEventsGenerator.setEventIdGenerator(eventIdGen);
    }
}
