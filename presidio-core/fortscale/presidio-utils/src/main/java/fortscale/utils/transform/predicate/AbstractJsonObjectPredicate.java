package fortscale.utils.transform.predicate;

import org.apache.commons.lang3.Validate;

public abstract class AbstractJsonObjectPredicate implements IJsonObjectPredicate {

    private String name;

    public AbstractJsonObjectPredicate(String name){
        this.name = Validate.notBlank(name, "name cannot be blank, empty or null.");
    }

    public String getName(){
        return name;
    }
}
