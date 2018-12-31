package presidio.data.generators.processentity;

import presidio.data.generators.common.IStringListGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * By default - the value will contain all possible categories in the list.
 * Use custom constructor when need to generate specific categories.
 */
public class ProcessCategoriesGenerator implements IStringListGenerator {
    private static final String[] ALL_PROCESS_CATEGORIES = {
            "WORD_PROCESSOR",
            "OFFICE",
            "RECONNAISSANCE_TOOL"
    };

    private List<String> value = Arrays.asList(ALL_PROCESS_CATEGORIES);

    public List<String> getNext(){
        return value;
    }

    public ProcessCategoriesGenerator() { }

    public ProcessCategoriesGenerator(String[] customList) {
        value = Arrays.asList(customList);
    }
}
