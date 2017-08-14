package presidio.data.generators.common;

public class StringCyclicValuesGenerator extends CyclicValuesGenerator<String> implements IStringGenerator{
    public StringCyclicValuesGenerator() {
        super();
    }

    public StringCyclicValuesGenerator(String value) {
        super(value);
    }

    public StringCyclicValuesGenerator(String[] values) {
        super(values);
    }
}
