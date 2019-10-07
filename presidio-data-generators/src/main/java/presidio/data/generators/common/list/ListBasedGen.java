package presidio.data.generators.common.list;

import com.google.common.collect.ImmutableList;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class ListBasedGen implements RangeGenerator<String> {

    private int fromIndex;
    private int size;
    private int currentIndex;
    protected UnaryOperator<String> formatter = e -> e;

    protected ListBasedGen(int fromIndex, int size, int limit) {
        if (fromIndex + size < limit) throw new IllegalArgumentException("Unable to allocate " + size
                + " values from index " + fromIndex + ". Max index is " + (limit-1));

        this.fromIndex = fromIndex;
        this.currentIndex = fromIndex;
        this.size = size;
    }

    abstract protected ImmutableList<String> getList();

    @Override
    public String getNextRandom() {
        int i = ThreadLocalRandom.current().nextInt(getStartIndex(), getStartIndex() + getSize());
        return formatter.apply(getList().get(i));
    }

    @Override
    public String getNextCyclic() {
        if (currentIndex >= fromIndex+size-1) {
            currentIndex = fromIndex;
        }
        return formatter.apply(getList().get(currentIndex++));
    }

    @Override
    public Function<String, String> getToString() {
        return e -> e;
    }

    @Override
    public int getStartIndex() {
        return fromIndex;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setFormatter(UnaryOperator<String> formatter) {
        this.formatter = formatter;
    }
}
