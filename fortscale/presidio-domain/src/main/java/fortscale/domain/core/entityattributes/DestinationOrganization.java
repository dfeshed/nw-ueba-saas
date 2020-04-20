package fortscale.domain.core.entityattributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DestinationOrganization extends EntityAttributes {

    public DestinationOrganization(@JsonProperty("name") String name) {
        super(name);
    }
}
