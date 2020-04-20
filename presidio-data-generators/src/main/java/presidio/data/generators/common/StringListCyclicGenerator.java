package presidio.data.generators.common;

import java.util.List;

public class StringListCyclicGenerator extends CyclicValuesGenerator<List<String>> implements IStringListGenerator{
    public StringListCyclicGenerator() {
        super();
    }

    public StringListCyclicGenerator(List<String> value) {
        super(value);
    }

    public StringListCyclicGenerator(List<String>[] values) {
        super(values);
    }
}
