package presidio.data.generators.common;

import org.apache.commons.lang3.RandomStringUtils;

public class NormalizedUsernameGenerator implements IStringGenerator{
    public NormalizedUsernameGenerator() {}

    public String getNext(){
        return RandomStringUtils.randomAlphanumeric(10) + "@" + RandomStringUtils.randomAlphanumeric(4) + ".com";
    }
}
