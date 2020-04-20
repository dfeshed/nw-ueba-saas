package presidio.data.generators.common;

/**
 * This class is one element data provider from a cyclic list of string values
 */
public class FixedIPsGenerator extends CyclicValuesGenerator<String> implements IStringGenerator {

    private final static String[] DEFAULT_VALUES = {"192.168.0.1"};

    public FixedIPsGenerator() {
        super(DEFAULT_VALUES);
    }

    public FixedIPsGenerator(String[] customList) {
        super(customList);
    }
}
