package presidio.data.generators.common.perf.lists;

import org.testng.Assert;

import java.util.function.Function;

public class LimitedListMapper<T> {
    private final int size;
    private Function<Integer, T> mapper;

    LimitedListMapper(int size, Function<Integer, T> indexToValue) {
        this.size = size;
        mapper= indexToValue;
    }

    public int size() {
        return size;
    }

    public T indexToValue(int index) {
        Assert.assertTrue(index < size);
        return mapper.apply(index);
    }
}
