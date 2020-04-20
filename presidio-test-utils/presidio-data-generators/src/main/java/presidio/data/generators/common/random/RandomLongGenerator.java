package presidio.data.generators.common.random;

import org.apache.commons.lang3.RandomUtils;
import presidio.data.generators.IBaseGenerator;

public class RandomLongGenerator implements IBaseGenerator<Long> {
    private int fromNumber, toNumber;

    public RandomLongGenerator(int fromNumber, int toNumber) {
        this.fromNumber = fromNumber;
        this.toNumber = toNumber;
    }

    @Override
    public Long getNext() {
        return RandomUtils.nextLong(fromNumber, toNumber);
    }
}
