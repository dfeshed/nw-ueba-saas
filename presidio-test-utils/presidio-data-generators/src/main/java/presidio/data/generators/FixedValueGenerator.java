package presidio.data.generators;

import presidio.data.generators.common.list.RangeGenerator;

import java.util.List;
import java.util.function.Function;

public class FixedValueGenerator<T> implements RangeGenerator<T> {

    private T object;

    public FixedValueGenerator(T object) {
        this.object = object;
    }


    @Override
    public T getNext() {
       return object;
    }

    @Override
    public List<T> getAllValues() {
        return List.of(object);
    }


    @Override
    public List<String> getAllValuesToString(Function<T, String> toString) {
        return List.of(toString.apply(object));
    }

    @Override
    public void reset() { }
}
