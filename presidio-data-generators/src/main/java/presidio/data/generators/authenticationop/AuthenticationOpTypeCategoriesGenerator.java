package presidio.data.generators.authenticationop;

import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.IStringListGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * By default - the value will contain all possible type categories in the list.
 * Use custom constructor when need to generate specific categories.
 */
public class AuthenticationOpTypeCategoriesGenerator implements IStringListGenerator {
    private static final String[] ALL_AUTH_OP_TYPE_CATEGORIES = {"INTERACTIVE_REMOTE"};

    private List<String> value = Arrays.asList(ALL_AUTH_OP_TYPE_CATEGORIES);

    public List<String> getNext(){
        return value;
    }

    public AuthenticationOpTypeCategoriesGenerator() { }

    public AuthenticationOpTypeCategoriesGenerator(String[] customList) {
        value = Arrays.asList(customList);
    }

}
