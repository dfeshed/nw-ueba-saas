package presidio.data.generators.user;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;


public class RandomObjectDNGenerator extends CyclicValuesGenerator<String> implements IStringGenerator{

    private final static String[] DEFAULT_VALUES = {"Test1", "Test2", "Test3", "Test4", "Test5"};

    public RandomObjectDNGenerator() {
        super(DEFAULT_VALUES);
    }

    @Override
    public String getNext(){
        return "ca=" + super.getNext() + ",CN=Users,";
    }

}
