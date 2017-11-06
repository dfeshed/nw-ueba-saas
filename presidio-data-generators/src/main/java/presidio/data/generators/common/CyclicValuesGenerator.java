package presidio.data.generators.common;

import java.util.NoSuchElementException;

/**
 * A generic generator that returns values of type T from a given array in a cyclic manner.
 */
public class CyclicValuesGenerator<T> {
    private T[] values;
    private int currentIdx;

    @SuppressWarnings("unchecked")
    public CyclicValuesGenerator() {
        this((T[])new Object[]{});
    }

    @SuppressWarnings("unchecked")
    public CyclicValuesGenerator(T value) {
        this((T[])new Object[]{value});
    }

    public CyclicValuesGenerator(T[] values) {
        this.values = values;
        this.currentIdx = -1;
    }

    public T getNext() {
        if (values.length == 0) {
            throw new NoSuchElementException("The array of values is empty.");
        }

        currentIdx++;
        if (currentIdx == values.length) currentIdx = 0;
        return values[currentIdx];
    }

    public void setValuesList(T[] values) {
        this.values = values;
        this.currentIdx = -1;
    }

    public T[] getValues() {
        return values;
    }

    public void reset() {
        currentIdx = -1;
    }
}
