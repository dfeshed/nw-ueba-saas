package presidio.data.generators.common.list.cyclic;

import presidio.data.generators.common.list.IndexBasedGen;

import java.util.function.Function;

public class CyclicRangeNumberGen<T extends Number> extends IndexBasedGen<T> {

    public CyclicRangeNumberGen(int fromIndex, int size, Function<Number, T> typeConverter) {
        super(fromIndex, size, typeConverter);
    }

    @Override
    public T getNext() {
        return getNextCyclic();
    }
}
