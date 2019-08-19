package fortscale.domain.core.entityattributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ja3 extends EntityAttributes {

    public Ja3(@JsonProperty("name") String name) {
        super(name);
    }
}
