package presidio.data.generators.common.list;

import presidio.data.generators.IBaseGenerator;

import java.util.List;
import java.util.function.Function;

public interface RangeGenerator<T> extends IBaseGenerator<T> {
    List<T> getAllValues();

    List<String> getAllValuesToString(Function<T, String> toString);

    void reset();
}
