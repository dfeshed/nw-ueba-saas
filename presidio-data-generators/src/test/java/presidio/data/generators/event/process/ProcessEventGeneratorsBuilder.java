package presidio.data.generators.event.process;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.RandomMultiEventGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessEntityGenerator;
import presidio.data.generators.processop.ProcessOperationGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class ProcessEventGeneratorsBuilder {

    /** USERS **/
    protected IUserGenerator normalUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange;
    protected List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange;
    protected IUserGenerator adminUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange;
    protected List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange;
    protected IUserGenerator serviceAccountUserGenerator;
    protected List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange;

    /** MACHINES **/
    protected  IMachineGenerator machineGenerator;

    /** Processes **/
    protected  List<FileEntity> nonImportantProcesses;



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
        this.normalUserGenerator = normalUserGenerator;
        this.normalUserActivityRange = normalUserActivityRange;
        this.normalUserAbnormalActivityRange = normalUserAbnormalActivityRange;
        this.adminUserGenerator = adminUserGenerator;
        this.adminUserActivityRange = adminUserActivityRange;
        this.adminUserAbnormalActivityRange = adminUserAbnormalActivityRange;
        this.serviceAccountUserGenerator = serviceAccountUserGenerator;
        this.serviceAcountUserActivityRange = serviceAcountUserActivityRange;
        this.machineGenerator = machineGenerator;
        this.nonImportantProcesses = nonImportantProcesses;
    }

    protected List<FileEntity> getFileEnities(Pair[] dirAndFilePair){
        List<FileEntity> ret = new ArrayList<>();
        ProcessFileEntityGenerator fileGenerator = new ProcessFileEntityGenerator(dirAndFilePair);
        for(int i = 0; i < dirAndFilePair.length; i++){
            ret.add(fileGenerator.getNext());
        }
        return ret;
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

    protected RandomMultiEventGenerator createRandomEventGenerator(ProcessEventsGenerator processEventsGenerator,
                                                                 List<MultiRangeTimeGenerator.ActivityRange> rangesList,
                                                                 double eventProbability,
                                                                 int timeIntervalForAbnormalTime,
                                                                 Instant startInstant,
                                                                 Instant endInstant) {
        List< RandomMultiEventGenerator.EventGeneratorProbability > listOfProbabilities = new ArrayList<>();
        RandomMultiEventGenerator.EventGeneratorProbability eventsProbabilityForNormalUsers =
                new RandomMultiEventGenerator.EventGeneratorProbability(processEventsGenerator, eventProbability);
        listOfProbabilities.add(eventsProbabilityForNormalUsers);



        RandomMultiEventGenerator randomEventsGenerator = new RandomMultiEventGenerator(listOfProbabilities,
                startInstant, endInstant, rangesList, Duration.ofMillis((int) (timeIntervalForAbnormalTime) ));
        return randomEventsGenerator;
    }

    public abstract List<AbstractEventGenerator<Event>> buildGenerators(Instant startInstant, Instant endInstant) throws GeneratorException;
}
