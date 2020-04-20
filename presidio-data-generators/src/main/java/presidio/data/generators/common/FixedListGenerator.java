package presidio.data.generators.common;

import java.util.List;


public class FixedListGenerator implements IStringListGenerator {

    private List<String> values;

    public FixedListGenerator(List<String> values) {
        this.values = values;
    }

    public List<String> getNext() {
        return values;
    }

}
