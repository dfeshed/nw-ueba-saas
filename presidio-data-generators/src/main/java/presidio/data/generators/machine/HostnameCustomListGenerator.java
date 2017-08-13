package presidio.data.generators.machine;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class HostnameCustomListGenerator extends CyclicValuesGenerator<String> implements IStringGenerator {

    public HostnameCustomListGenerator(String[] customList) {
        super(customList);
    }
}
