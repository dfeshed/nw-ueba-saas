package presidio.data.generators.machine;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class HostnameCustomListGenerator extends AbstractCyclicValuesGenerator implements IStringGenerator {

    public HostnameCustomListGenerator(String[] customList) {
        super(customList);
    }
}
