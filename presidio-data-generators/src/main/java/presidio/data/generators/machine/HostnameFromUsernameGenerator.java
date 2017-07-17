package presidio.data.generators.machine;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.utils.StringGeneratorUtils;

public class HostnameFromUsernameGenerator extends AbstractCyclicValuesGenerator<String> implements IStringGenerator {

    public HostnameFromUsernameGenerator(String username) {
        super(username + "_src");
    }

    public HostnameFromUsernameGenerator(String username, int numberOfHosts) {
        super(StringGeneratorUtils.buildUniqueAlphabetStrings(username, "src", numberOfHosts));
    }

 }
