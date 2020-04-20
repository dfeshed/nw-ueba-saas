package presidio.data.generators.machine;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.MachineEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MachineGeneratorDoubleWeightRouter implements IMachineGenerator{

    private List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs;
    private double[] points;
    private double total;
    private Random rand;


    public MachineGeneratorDoubleWeightRouter(List<Pair<IMachineGenerator, Double>> machineGeneratorToFrequencyWeightPairs){
        this.machineGeneratorToFrequencyWeightPairs = machineGeneratorToFrequencyWeightPairs;
        this.points = new double[machineGeneratorToFrequencyWeightPairs.size()];
        total = 0;
        for(int i = 0; i < machineGeneratorToFrequencyWeightPairs.size(); i++){
            Pair<IMachineGenerator, Double> pair = machineGeneratorToFrequencyWeightPairs.get(i);
            total += pair.getValue();
            points[i] = total;
        }
        rand = new Random(0);
    }

    @Override
    public MachineEntity getNext() {
        double point = rand.nextDouble()*total;
        int cursor = Arrays.binarySearch(points, point);
        if(cursor < 0){
            cursor = -cursor - 1;
        }
        return machineGeneratorToFrequencyWeightPairs.get(cursor).getKey().getNext();
    }
}
