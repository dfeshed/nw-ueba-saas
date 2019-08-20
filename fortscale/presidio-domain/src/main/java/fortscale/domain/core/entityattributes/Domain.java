package fortscale.domain.core.entityattributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Domain extends EntityAttributes {

    public Domain(@JsonProperty("name") String name) {
        super(name);    }
}
