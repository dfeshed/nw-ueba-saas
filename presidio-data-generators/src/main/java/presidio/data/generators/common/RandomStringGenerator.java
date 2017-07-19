package presidio.data.generators.common;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomStringGenerator implements IStringGenerator {
    private final int randomStringLength;

    public RandomStringGenerator(){
        randomStringLength = 10;
    }

    public RandomStringGenerator(int randomStringLength){
        this.randomStringLength = randomStringLength;
    }

    public String getNext(){
        return RandomStringUtils.randomAlphanumeric(randomStringLength);
    }
}
