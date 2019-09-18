package fortscale.utils.transform;

import fortscale.utils.reflection.PresidioReflectionUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;


public abstract class AbstractJsonObjectTransformer implements IJsonObjectTransformer {

    private String name;

    public AbstractJsonObjectTransformer(String name){
        this.name = Validate.notBlank(name, "name cannot be blank, empty or null.");
    }


    @Override
    public String getName(){
        return name;
    }
}
