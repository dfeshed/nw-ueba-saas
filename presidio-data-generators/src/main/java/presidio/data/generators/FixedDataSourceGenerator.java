package presidio.data.generators;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

/**
 * This class is one element data provider from a cyclic list of string values
 */
public class FixedDataSourceGenerator extends AbstractCyclicValuesGenerator implements IStringGenerator {
    private final static String[] DEFAULT_VALUES = {"DefaultDS"};

    public FixedDataSourceGenerator() {
        super(DEFAULT_VALUES);
    }

    public FixedDataSourceGenerator(String[] customList) {
        super(customList);
    }
}



