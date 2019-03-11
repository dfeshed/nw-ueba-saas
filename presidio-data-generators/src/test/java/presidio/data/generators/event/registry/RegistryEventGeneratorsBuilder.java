package presidio.data.generators.event.registry;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.RandomMultiEventGenerator;
import presidio.data.generators.event.process.CyclicOperationTypeGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessEntityGenerator;
import presidio.data.generators.registryentry.IRegistryEntryGenerator;
import presidio.data.generators.registryop.RegistryOperationGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class RegistryEventGeneratorsBuilder {

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
    protected IMachineGenerator machineGenerator;

    /** Processes **/
    protected  List<FileEntity> nonImportantProcesses;

    /** registry keys **/
    protected Map<String, List<String>> registryKeyGroupToRegistryKey;
    protected Map<String, List<String>> registryKeyToValueNamesMap;

    private double probabilityMultiplier;
    private double usersMultiplier;



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
        this.registryKeyGroupToRegistryKey = registryKeyGroupToRegistryKey;
        this.registryKeyToValueNamesMap = registryKeyToValueNamesMap;
        this.probabilityMultiplier = 1;
        this.usersMultiplier = 1;
    }

    public void setProbabilityMultiplier(double probabilityMultiplier) {
        this.probabilityMultiplier = probabilityMultiplier;
    }

    public double getProbabilityMultiplier() {
        return probabilityMultiplier;
    }

    public double getUsersMultiplier() {
        return usersMultiplier;
    }

    public void setUsersMultiplier(double usersMultiplier) {
        this.usersMultiplier = usersMultiplier;
    }

    protected List<FileEntity> getFileEnities(Pair[] dirAndFilePair){
        List<FileEntity> ret = new ArrayList<>();
        ProcessFileEntityGenerator fileGenerator = new ProcessFileEntityGenerator(dirAndFilePair);
        for(int i = 0; i < dirAndFilePair.length; i++){
            ret.add(fileGenerator.getNext());
        }
        return ret;
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

    protected RandomMultiEventGenerator createRandomEventGenerator(RegistryEventsGenerator registryEventsGenerator,
                                                                   List<MultiRangeTimeGenerator.ActivityRange> rangesList,
                                                                   double eventProbability,
                                                                   int timeIntervalForAbnormalTime,
                                                                   Instant startInstant,
                                                                   Instant endInstant) {
        List< RandomMultiEventGenerator.EventGeneratorProbability > listOfProbabilities = new ArrayList<>();
        RandomMultiEventGenerator.EventGeneratorProbability eventsProbabilityForNormalUsers =
                new RandomMultiEventGenerator.EventGeneratorProbability(registryEventsGenerator, eventProbability*getProbabilityMultiplier());
        listOfProbabilities.add(eventsProbabilityForNormalUsers);



        RandomMultiEventGenerator randomEventsGenerator = new RandomMultiEventGenerator(listOfProbabilities,
                startInstant, endInstant, rangesList, Duration.ofMillis((int) (timeIntervalForAbnormalTime) ));
        return randomEventsGenerator;
    }

    public abstract List<AbstractEventGenerator<Event>> buildGenerators(Instant startInstant, Instant endInstant) throws GeneratorException;
}
