package presidio.data.generators.activedirectoryop;

import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.generators.common.IStringListGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * By default - the value will contain all possible type categories in the list.
 * Use custom constructor when need to generate specific categories.
 */
public class ActiveDirectoryOpTypeCategoriesGenerator implements IStringListGenerator {
    private static final String[] ALL_ACTIVEDIRECTORY_OP_TYPE_CATEGORIES = {
            ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value,
            ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP.value,
            ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_ADD.value,
            ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_REMOVE.value
    };

    private List<String> value = Arrays.asList(ALL_ACTIVEDIRECTORY_OP_TYPE_CATEGORIES);

    public List<String> getNext(){
        return value;
    }

    public ActiveDirectoryOpTypeCategoriesGenerator() { }

    public ActiveDirectoryOpTypeCategoriesGenerator(String[] customList) {
        value = Arrays.asList(customList);
    }
}
