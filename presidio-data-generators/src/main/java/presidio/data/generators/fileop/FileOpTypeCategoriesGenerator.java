package presidio.data.generators.fileop;

import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.IStringListGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * By default - the value will contain all possible type categories in the list.
 * Use custom constructor when need to generate specific categories.
 */
public class FileOpTypeCategoriesGenerator implements IStringListGenerator {
    private static final String[] ALL_FILE_OP_TYPE_CATEGORIES = {
            FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value,
            FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value
    };

    private List<String> value = Arrays.asList(ALL_FILE_OP_TYPE_CATEGORIES);

    public List<String> getNext(){
        return value;
    }

    public FileOpTypeCategoriesGenerator() { }

    public FileOpTypeCategoriesGenerator(String[] customList) {
        value = Arrays.asList(customList);
    }

}
