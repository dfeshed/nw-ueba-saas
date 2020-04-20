package presidio.data.generators.common.random;

import org.apache.commons.lang3.RandomUtils;
import presidio.data.generators.IBaseGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomListElementGenerator<T> implements IBaseGenerator<T> {
    private final List<T> list;

    public RandomListElementGenerator(List<T> list) {
        this.list = list;
    }

    @Override
    public T getNext() {
        return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
    }
}
