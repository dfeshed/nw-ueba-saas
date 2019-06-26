package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MachineGeneratorRouter implements IMachineGenerator{
    private List<IMachineGenerator> machineGenerators;
    private Random random;
    public MachineGeneratorRouter(List<MachineGeneratorWeight> machineGeneratorWeights){
        machineGenerators = new ArrayList<>();
        for(MachineGeneratorWeight machineGeneratorWeight: machineGeneratorWeights){
            for(int i = 0; i < machineGeneratorWeight.getWeight(); i++){
                machineGenerators.add(machineGeneratorWeight.getMachineGenerator());
            }
        }
        random = new Random(0);
    }

    @Override
    public MachineEntity getNext() {
        IMachineGenerator machineGenerator = machineGenerators.get(random.nextInt(machineGenerators.size()));
        return machineGenerator.getNext();
    }


    public static class MachineGeneratorWeight{
        private int weight;
        private IMachineGenerator machineGenerator;

        public MachineGeneratorWeight(int weight, IMachineGenerator machineGenerator){
            this.weight = weight;
            this.machineGenerator = machineGenerator;
        }

        public int getWeight() {
            return weight;
        }

        public IMachineGenerator getMachineGenerator() {
            return machineGenerator;
        }
    }
}
