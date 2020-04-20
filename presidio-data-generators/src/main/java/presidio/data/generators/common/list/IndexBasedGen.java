package presidio.data.generators.common.list;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public abstract class IndexBasedGen<T extends Number> implements RangeGenerator<T> {

    private final int fromIndex;
    private final int size;
    private Number currentIndex;
    private final Function<Number, T> TYPE_CONVERTER;

    public IndexBasedGen(int fromIndex, int size, Function<Number, T> typeConverter) {
        this.fromIndex = fromIndex;
        this.currentIndex = fromIndex;
        this.size = size;
        TYPE_CONVERTER = typeConverter;
    }

    @Override
    public List<T> getAllValues() {
        return IntStream.range(fromIndex, fromIndex + size).boxed().map(e -> getNextCyclic()).collect(toList());
    }

    @Override
    public List<String> getAllValuesToString(Function<T, String> toString) {
        return getAllValues().stream().map(toString).collect(toList());
    }

    @Override
    public void reset() {
        currentIndex = fromIndex;
    }

    protected T getNextCyclic() {
        if (currentIndex.intValue() >= fromIndex+size) {
            currentIndex = fromIndex;
        }
        currentIndex = currentIndex.intValue() + 1;
        return TYPE_CONVERTER.apply(currentIndex);
    }

    protected T getNextRandom() {
        return TYPE_CONVERTER.apply(ThreadLocalRandom.current()
                .nextInt(fromIndex, fromIndex + size));
    }

}
