package presidio.data.generators.common.random;

import org.apache.commons.lang3.RandomUtils;
import presidio.data.generators.IBaseGenerator;

public class RandomIntegerGenerator implements IBaseGenerator<Integer> {
    private int fromNumber, toNumber;

    public RandomIntegerGenerator(int fromNumber, int toNumber) {
        this.fromNumber = fromNumber;
        this.toNumber = toNumber;
    }

    @Override
    public Integer getNext() {
        return RandomUtils.nextInt(fromNumber, toNumber);
    }
}
