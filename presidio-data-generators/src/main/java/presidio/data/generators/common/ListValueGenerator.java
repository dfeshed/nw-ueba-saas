package presidio.data.generators.common;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.IBaseGenerator;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;

public class ListValueGenerator<T> implements IBaseGenerator<T> {

    private final boolean isRandomNext;
    private final ImmutableList<T> LIST_OF_ELEMENTS;
    private AtomicInteger currentIndex = new AtomicInteger(0);

    public ListValueGenerator(List<T> listOfElements) {
        this(listOfElements, false);
    }

    public ListValueGenerator(List<T> listOfElements, boolean isRandomNext) {
        Objects.requireNonNull(listOfElements);
        assert !listOfElements.isEmpty();
        LIST_OF_ELEMENTS = ImmutableList.copyOf(listOfElements);
        this.isRandomNext = isRandomNext;
    }

    @Override
    public T getNext() {
        return isRandomNext ? getNextRandom() : getNextCyclic();
    }

    public List<T> getAllValues() {
        return LIST_OF_ELEMENTS;
    }

    private T getNextRandom() {
        return LIST_OF_ELEMENTS.get(ThreadLocalRandom.current().nextInt(LIST_OF_ELEMENTS.size()));
    }

    private IntBinaryOperator accumulator = (current, size) -> current >= size ? 0 : current + 1;

    private T getNextCyclic() {
        return LIST_OF_ELEMENTS.get(currentIndex.getAndAccumulate(LIST_OF_ELEMENTS.size() - 1, accumulator));
    }
}
