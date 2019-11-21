package presidio.data.generators.common;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.IBaseGenerator;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ListValueGenerator<T> implements IBaseGenerator<T> {

    private final boolean isRandomNext;
    private final ImmutableList<T> LIST_OF_ELEMENTS;
    private int currentIndex;

    public ListValueGenerator(List<T> listOfElements) {
        this(listOfElements, false);
    }

    public ListValueGenerator(List<T> listOfElements, boolean isRandomNext) {
        Objects.requireNonNull(listOfElements);
        LIST_OF_ELEMENTS = ImmutableList.copyOf(listOfElements);
        currentIndex = 0;
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
        int i = ThreadLocalRandom.current().nextInt(LIST_OF_ELEMENTS.size());
        return LIST_OF_ELEMENTS.get(i);
    }

    private T getNextCyclic() {
        if (currentIndex >= LIST_OF_ELEMENTS.size()) {
            currentIndex = 0;
        }
        return LIST_OF_ELEMENTS.get(currentIndex++);
    }
}
