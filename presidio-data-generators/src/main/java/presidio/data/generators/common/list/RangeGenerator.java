package presidio.data.generators.common.list;

import presidio.data.generators.IBaseGenerator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public interface RangeGenerator<T> extends IBaseGenerator<T> {
    int getStartIndex();
    int getSize();
    Function<T, String> getToString();

    T getNextCyclic();
    T getNextRandom();

    default List<T> getAllValues() {
        return IntStream.range(getStartIndex(), getStartIndex() + getSize()).boxed().map(e -> getNextCyclic()).collect(toList());
    }

    default List<String> getStringValues() {
        return getAllValues().stream().map(getToString()).collect(toList());
    }

}
