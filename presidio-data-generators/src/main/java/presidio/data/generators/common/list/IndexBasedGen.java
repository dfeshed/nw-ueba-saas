package presidio.data.generators.common.list;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public abstract class IndexBasedGen implements RangeGenerator<Number> {

    private int fromIndex;
    private int size;
    private int currentIndex;

    public IndexBasedGen(int fromIndex, int size) {
        this.fromIndex = fromIndex;
        this.currentIndex = fromIndex;
        this.size = size;
    }

    @Override
    public int getStartIndex() {
        return fromIndex;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Function<Number, String> getToString() {
        return String::valueOf;
    }

    @Override
    public Number getNextCyclic() {
        if (currentIndex >= fromIndex+size-1) {
            currentIndex = fromIndex;
        }
        return currentIndex;
    }

    @Override
    public Number getNextRandom() {
        return ThreadLocalRandom.current().nextInt(getStartIndex(), getStartIndex() + getSize());
    }

}
