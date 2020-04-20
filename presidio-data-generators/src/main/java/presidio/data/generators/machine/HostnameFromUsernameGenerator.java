package presidio.data.generators.machine;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.utils.StringUtils;

public class HostnameFromUsernameGenerator extends CyclicValuesGenerator<String> implements IStringGenerator {

    public HostnameFromUsernameGenerator(String username) {
        super(username + "_src");
    }

    public HostnameFromUsernameGenerator(String username, int numberOfHosts) {
        super(StringUtils.buildUniqueAlphabetStrings(username, "src", numberOfHosts));
    }

 }
