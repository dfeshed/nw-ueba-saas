package presidio.data.generators.common.list.random;

import presidio.data.generators.common.list.IndexBasedGen;

import java.util.function.Function;

public class RandomRangeNumberGen<T extends Number> extends IndexBasedGen<T> {

    public RandomRangeNumberGen(int fromIndex, int size, Function<Number, T> typeConverter) {
        super(fromIndex, size, typeConverter);
    }

    @Override
    public T getNext() {
        return getNextRandom();
    }
}
