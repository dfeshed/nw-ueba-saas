package presidio.data.generators.machine;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class CustomListGenerator extends AbstractCyclicValuesGenerator implements IStringGenerator {

    public CustomListGenerator(String[] customList) {
        super(customList);
    }
}
