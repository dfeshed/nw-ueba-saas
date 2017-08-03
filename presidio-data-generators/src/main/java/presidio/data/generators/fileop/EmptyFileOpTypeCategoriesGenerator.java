package presidio.data.generators.fileop;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringListGenerator;

import java.util.Arrays;
import java.util.List;

public class EmptyFileOpTypeCategoriesGenerator implements IStringListGenerator {
    private List<String> value = Arrays.asList(new String[] {""});

    public List<String> getNext(){
        return value;
    }

    public EmptyFileOpTypeCategoriesGenerator() throws GeneratorException {
    }
}
