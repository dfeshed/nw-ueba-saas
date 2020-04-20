package presidio.data.generators.common.precentage;


import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.utils.MathUtils;


public abstract class AbstractStringPercentageGenerator extends CyclicValuesGenerator<String> {
    private static final int[] DEFAULT_PERCENT = new int[] {100, 0};

    public AbstractStringPercentageGenerator(String option) throws GeneratorException {
        super(buildValuesList(new String[] {option, "Other"}, DEFAULT_PERCENT));
    }

    public AbstractStringPercentageGenerator(String[] options, int percents[]) throws GeneratorException {
        super(buildValuesList(options, percents));
    }

    private static String[] buildValuesList(String[] options, int[] percents) throws GeneratorException {
        String[] ratedOptList;

        // Validate parameters
        if (options.length != percents.length) throw new GeneratorException("Generator Exception: Number of options do not match the number of percentages provided.");

        for (int i = 0; i< percents.length; i++) {
            if (percents[i] < 0 || percents[i] > 100)
                throw new GeneratorException("The parameter value should be between 0 and 100");
        }

        // build smallest array of options in given whole percentages (at most - 100 elements)
        int gcd = MathUtils.gcd(percents);

        // divide all percents values to the gcd and summ up
        int rates[] = new int[percents.length];
        int ratedOptListSize = 0;
        for (int i = 0; i < percents.length; i++) {
            rates[i] = percents[i] / gcd;
            ratedOptListSize += rates[i];
        }

        //create options array and fill
        ratedOptList = new String[ratedOptListSize];
        int currentOption = 0;
        for (int i = 0; i < rates.length; i++) {
            for (int j = 0; j < rates[i]; j++) {
                ratedOptList[currentOption++] = options[i];
            }
        }
        return ratedOptList;
    }
}
