package presidio.data.generators;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public interface IBaseGenerator<T> {
    T getNext();

    default List<T> nextValues(int numOfValues) {
        return IntStream.range(0, numOfValues).boxed().map(e -> getNext()).collect(toList());
    }
}
