package presidio.data.generators.common.list;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public abstract class ListBasedGen<T> implements RangeGenerator<T> {

    private final int fromIndex;
    private final int size;
    private int currentIndex;
    public UnaryOperator<T> formatter = e -> e;


    protected ListBasedGen(int fromIndex, int size, int limit) {
        if (fromIndex + size > limit) throw new IllegalArgumentException("Unable to allocate " + size
                + " values from index " + fromIndex + ". Max index is " + (limit-1));

        this.fromIndex = fromIndex;
        this.currentIndex = fromIndex;
        this.size = size;
    }

    abstract protected ImmutableList<T> getList();

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

    protected T getNextRandom() {
        int i = ThreadLocalRandom.current().nextInt(fromIndex, fromIndex +size);
        return formatter.apply(getList().get(i));
    }

    protected T getNextCyclic() {
        if (currentIndex >= fromIndex+size) {
            currentIndex = fromIndex;
        }
        return formatter.apply(getList().get(currentIndex++));
    }

}
