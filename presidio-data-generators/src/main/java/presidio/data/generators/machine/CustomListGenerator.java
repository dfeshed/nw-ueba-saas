package presidio.data.generators.machine;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class CustomListGenerator extends CyclicValuesGenerator<String> implements IStringGenerator {

    public CustomListGenerator(String[] customList) {
        super(customList);
    }
}
