package presidio.data.domain.event;

import java.util.List;

/**
 * Created by YaronDL on 8/7/2017.
 */
public class OperationType {
    private String name;
    private List<String> categories;

    public OperationType(String name, List<String> categories) {
        this.name = name;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public List<String> getCategories() {
        return categories;
    }


    @Override
    public String toString() {
        return  "Operation Type: " + name + "," + categories;
    }
}
