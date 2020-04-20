package presidio.data.generators.common;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class CyclicValuesRandomGenerator<T>{

    private List<Pair<T, Double>> valuesToProbabilityPairs;
    private int cursorIndex;
    private Random random;

    public CyclicValuesRandomGenerator(List<Pair<T, Double>> valuesToProbabilityPairs){
        this.valuesToProbabilityPairs = validateValuesToProbabilityPairs(valuesToProbabilityPairs);
        random = new Random(0);
        cursorIndex = 0;
    }

    private List<Pair<T, Double>> validateValuesToProbabilityPairs(List<Pair<T, Double>> valuesToProbabilityPairs){
        Validate.notEmpty(valuesToProbabilityPairs);
        for(Pair<T, Double> valToProb: valuesToProbabilityPairs){
            if(valToProb == null){
                throw new IllegalArgumentException("valuesToProbabilityPairs contains a null pair");
            }
            if(valToProb.getKey() == null){
                throw new IllegalArgumentException("valuesToProbabilityPairs contains a null in the pair key");
            }

            if(valToProb.getValue() == null){
                throw new IllegalArgumentException("valuesToProbabilityPairs contains a null in the pair value");
            }

            if(valToProb.getValue() <= 0 || valToProb.getValue() > 1){
                throw new IllegalArgumentException("valuesToProbabilityPairs contains a probability which not in the range of (0-1]");
            }
        }

        return valuesToProbabilityPairs;
    }

    public T getNext() {
        T ret = null;
        while (ret == null){
            if(random.nextDouble() <= valuesToProbabilityPairs.get(cursorIndex).getValue()){
                ret = valuesToProbabilityPairs.get(cursorIndex).getKey();
            }
            cursorIndex++;
        }
        return ret;
    }
}
