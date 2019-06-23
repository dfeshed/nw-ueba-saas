package presidio.data.generators.event;

import presidio.data.domain.FileEntity;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileentity.RandomFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.MachineGeneratorRouter;
import presidio.data.generators.machine.RandomMultiMachineEntityGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;

import java.time.Instant;
import java.util.*;

public abstract class EndPointPerformanceStabilityScenario extends UserOrientedPerformanceStabilityScenario{

    /** MACHINES **/
    protected IMachineGenerator machineGenerator;

    /** Processes **/
    protected List<FileEntity> nonImportantProcesses;

    public EndPointPerformanceStabilityScenario(Instant startInstant, Instant endInstant,
                                                double probabilityMultiplier, double usersMultiplier) {
        super(startInstant, endInstant, probabilityMultiplier, usersMultiplier);
    }

    public void init() throws GeneratorException {
        /** MACHINES **/
        machineGenerator = createMachineGenerator();

        /** Processes **/
        nonImportantProcesses = generateFileEntities();

        super.init();
    }

    private IMachineGenerator createMachineGenerator(){
        IMachineGenerator src100MachinesGenerator = createNonDesktopMachineGenerator();

        List<MachineGeneratorRouter.MachineGeneratorWeight> machineGeneratorWeights = new ArrayList<>();
        machineGeneratorWeights.add(new MachineGeneratorRouter.MachineGeneratorWeight(5, src100MachinesGenerator));
        machineGeneratorWeights.add(new MachineGeneratorRouter.MachineGeneratorWeight(95, new UserDesktopGenerator()));
        MachineGeneratorRouter machineGeneratorRouter = new MachineGeneratorRouter(machineGeneratorWeights);

        return machineGeneratorRouter;
    }

    private IMachineGenerator createNonDesktopMachineGenerator(){
        return new RandomMultiMachineEntityGenerator(
                Arrays.asList("100m_domain1", "100m_domain2", "100m_domain3", "100m_domain4", "100m_domain5",
                        "100m_domain6", "100m_domain7", "100m_domain8", "100m_domain9", "100m_domain10"),
                10, "5machines_",
                10, "src");
    }

    private List<FileEntity> generateFileEntities(){
        RandomFileEntityGenerator randomFileEntityGenerator =
                new RandomFileEntityGenerator(1000, "dir", "",
                        10000, "proc", ".exe");
        Set<FileEntity> fileEntitySet = new HashSet<>();
        while(fileEntitySet.size() < 10000){
            fileEntitySet.add(randomFileEntityGenerator.getNext());
        }
        return new ArrayList<>(fileEntitySet);
    }
}
