package presidio.data.generators.machine;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.utils.StringGeneratorUtils;

public class HostnameFromUsernameGenerator extends CyclicValuesGenerator<String> implements IStringGenerator {

    public HostnameFromUsernameGenerator(String username) {
        super(username + "_src");
    }

    public HostnameFromUsernameGenerator(String username, int numberOfHosts) {
        super(StringGeneratorUtils.buildUniqueAlphabetStrings(username, "src", numberOfHosts));
    }

 }
