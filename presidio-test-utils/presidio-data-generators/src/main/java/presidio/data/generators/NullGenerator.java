package presidio.data.generators;

import presidio.data.generators.common.list.RangeGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NullGenerator<T> implements RangeGenerator<T> {
    @Override
    public T getNext() {
       return null;
    }

    @Override
    public List<T> getAllValues() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getAllValuesToString(Function<T, String> toString) {
        return new ArrayList<>();
    }

    @Override
    public void reset() { }
}
