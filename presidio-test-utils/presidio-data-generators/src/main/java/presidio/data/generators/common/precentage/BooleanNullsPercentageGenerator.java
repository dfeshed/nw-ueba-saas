package presidio.data.generators.common.precentage;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IBooleanGenerator;
import presidio.data.generators.utils.MathUtils;


/**
 * Generates specified percent of null values, the rest - false
 */
public class BooleanNullsPercentageGenerator extends CyclicValuesGenerator<Boolean> implements IBooleanGenerator{
    public BooleanNullsPercentageGenerator(int percent) {
        super(buildValuesList(percent));
    }

    public BooleanNullsPercentageGenerator() throws GeneratorException {
        super(buildValuesList(100));
    }

    private static Boolean[] buildValuesList(int percent){
        // build smallest array of options in given whole percentages (at most - 100 elements)

        int gcd = MathUtils.gcd(percent,100); // find common denominator
        Boolean[] values = new Boolean[100/gcd];

        // fill the array with true/false values in ratio that gives required percent of true values
        for (int i = 0; i < 100/gcd; i++){ values[i] = (i < percent/gcd) ? null : false; }
        return values;
    }
}
