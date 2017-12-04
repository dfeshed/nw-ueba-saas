package presidio.data.domain.event;

import java.util.Collections;
import java.util.List;

public class OperationType {
    private String name;
    private List<String> categories;

    public OperationType(String name){
        this(name, Collections.emptyList());
    }

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
