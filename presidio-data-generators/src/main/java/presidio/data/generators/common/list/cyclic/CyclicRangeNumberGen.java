package presidio.data.generators.common.list.cyclic;

import presidio.data.generators.common.list.IndexBasedGen;

import java.util.function.Function;

public class CyclicRangeNumberGen<T extends Number> extends IndexBasedGen {

    private final Function<Number, T> typeConverter;

    public CyclicRangeNumberGen(int fromIndex, int size, Function<Number, T> typeConverter) {
        super(fromIndex, size);
        this.typeConverter = typeConverter;
    }

    @Override
    public T getNext() {
        return typeConverter.apply(getNextCyclic());
    }
}
