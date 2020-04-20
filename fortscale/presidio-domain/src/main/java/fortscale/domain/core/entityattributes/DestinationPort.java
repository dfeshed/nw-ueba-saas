package fortscale.domain.core.entityattributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DestinationPort extends EntityAttributes {

    public DestinationPort(@JsonProperty("name") String name) {
        super(name);
    }
}
