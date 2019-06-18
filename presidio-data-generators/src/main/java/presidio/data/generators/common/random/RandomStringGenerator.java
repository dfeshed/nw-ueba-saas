package presidio.data.generators.common.random;

import org.apache.commons.lang3.RandomStringUtils;
import presidio.data.generators.IBaseGenerator;

public class RandomStringGenerator implements IBaseGenerator<String> {
    private int minLength, maxLength;

    public RandomStringGenerator(int minLength, int maxLength) {
        this.maxLength = maxLength;
        this.minLength = minLength;
    }

    @Override
    public String getNext() {
        return RandomStringUtils.randomAlphanumeric(minLength,maxLength);
    }
}
