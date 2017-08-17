package presidio.data.generators.event.activedirectory;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;


public class ObjectNameGenerator extends CyclicValuesGenerator<String> implements IStringGenerator {
    private final static String[] DEFAULT_VALUES = {"Test1", "Test2", "Test3", "Test4", "Test5"};

    public ObjectNameGenerator() {
        super(DEFAULT_VALUES);
    }

    @Override
    public String getNext() {
        return super.getNext();
    }
}
