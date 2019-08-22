package fortscale.domain.core.entityattributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DestinationAsn extends EntityAttributes {

    public DestinationAsn(@JsonProperty("name") String name) {
        super(name);
    }
}
