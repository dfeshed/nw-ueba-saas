package presidio.data.generators.common.precentage;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IBooleanGenerator;
import presidio.data.generators.utils.MathUtils;


/**
 *
 */
public class BooleanPercentageGenerator extends CyclicValuesGenerator<Boolean> implements IBooleanGenerator{
    public BooleanPercentageGenerator(int percent) {
        super(buildValuesList(percent));
    }

    public BooleanPercentageGenerator() throws GeneratorException {
        // All true
        super(new Boolean[] {true});
    }

    private static Boolean[] buildValuesList(int percent){

        if (percent <= 0) return new Boolean[] {false}; // All false

        // build smallest array of options in given whole percentages (at most - 100 elements)
        int gcd = MathUtils.gcd(percent,100); // find common denominator
        Boolean[] values = new Boolean[100/gcd];

        // fill the array with true/false values in ratio that gives required percent of true values
        for (int i = 0; i < 100/gcd; i++){ values[i] = (i < percent/gcd); }
        return values;
    }
}
