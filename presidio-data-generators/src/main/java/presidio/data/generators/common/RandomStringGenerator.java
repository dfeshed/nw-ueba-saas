package presidio.data.generators.common;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomStringGenerator implements IStringGenerator {

    public RandomStringGenerator(){}

    public String getNext(){
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
