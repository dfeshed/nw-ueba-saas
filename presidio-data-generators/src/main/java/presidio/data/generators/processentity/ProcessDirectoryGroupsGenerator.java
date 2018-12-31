package presidio.data.generators.processentity;

import presidio.data.generators.common.IStringListGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * By default - the value will contain all possible categories in the list.
 * Use custom constructor when need to generate specific categories.
 */
public class ProcessDirectoryGroupsGenerator implements IStringListGenerator {
    private static final String[] ALL_PROCESS_DIRECTORY_GROUPS = {
            "system32",
            "tmp",
            "downloads"
    };

    private List<String> value = Arrays.asList(ALL_PROCESS_DIRECTORY_GROUPS);

    public List<String> getNext(){
        return value;
    }

    public ProcessDirectoryGroupsGenerator() { }

    public ProcessDirectoryGroupsGenerator(String[] customList) {
        value = Arrays.asList(customList);
    }
}
